import fs from "fs";
import path from "path";
import { exec, spawn } from "child_process";

export const videoController = {
    getAllVideos(req, res){
        try {
            const videosPath = path.join("..", "processor", "src", "main", "resources");
            if (!fs.existsSync(videosPath)) {
                return res.status(404).json({ error: "Videos folder not found" });
            }
            
            // Get all .mp4 files from the resources folder
            const files = fs.readdirSync(videosPath);
            const videoNames = files
                .filter(file => file.endsWith(".mp4"))
                .map(file => file.replace(".mp4", ""));
            
            res.status(200).json(videoNames);
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    },
    
processVideo(req, res) {
    const { videoName } = req.params;

    // Step 1: Check for video name
    if (!videoName) {
      return res.status(404).json({ error: "No video name provided" });
    }

    // Step 2: Check if video file exists
    const videoPath = path.join("..", "processor", "src", "main", "resources", `${videoName}.mp4`);
    if (!fs.existsSync(videoPath)) {
      return res.status(404).json({ error: "Video not found" });
    }

    // Step 3: Generate thumbnail (if possible). If thumbnail generation fails (for
    // example missing JavaFX runtime), continue and only log the error — do not
    // abort the processing job.
    const jarPath = path.join("..", "processor", "target", "centroid-finder-1.0-SNAPSHOT.jar");
    const thumbCommand = `java --enable-native-access=ALL-UNNAMED --module-path "../javafx-sdk-25.0.1/lib" --add-modules javafx.controls,javafx.fxml,javafx.media -cp "${jarPath}" io.jessechum.centroidfinder.ThumbNailGenerator "${videoPath}"`;
    exec(thumbCommand, (thumbError, thumbOutput, thumbStderr) => {
      if (thumbError) {
        console.warn("Thumbnail generation failed", thumbStderr);
        return res.status(500).json({ error: "Thumbnail generation failed" });
      }
      console.log("Thumbnail generation complete:", thumbOutput);
    return res.status(200).json({
      message: "Thumbnail generated successfully",
      output: thumbOutput.trim()
      });
    });
    // run the binarizer
  },

  // POST /process/:videoName
  // Starts processing in a detached background Java process and immediately
  // returns a jobId. Expects JSON body with optional `threshold` and `color`.
  startProcess(req, res) {
    const { videoName } = req.params;
    const { threshold, color } = req.body || {};

    if (!videoName) {
      return res.status(400).json({ error: "No video name provided" });
    }

    const videoPath = path.join("..", "processor", "src", "main", "resources", `${videoName}.mp4`);
    if (!fs.existsSync(videoPath)) {
      return res.status(404).json({ error: "Video not found" });
    }

    const jarPath = path.join("..", "processor", "target", "centroid-finder-1.0-SNAPSHOT.jar");
    const outputCsv = path.join("..", "output", `${videoName}.csv`);
    const targetColor = color;
    const thr = threshold

    const jobId = `${videoName}-${Date.now()}`;

    // Build java args and spawn detached process so we return immediately.
    const args = ["-cp", jarPath, "io.jessechum.centroidfinder.VideoApp", videoPath, outputCsv, targetColor, thr];
    try {
      const child = spawn("java", args, { detached: true, stdio: "ignore" });
      child.unref();

       try {
        const statusFile = path.join("src", "processed", "status.json");
        let statuses = [];
        if (fs.existsSync(statusFile)) {
          const raw = fs.readFileSync(statusFile, "utf8");
          try { statuses = JSON.parse(raw || "[]"); } catch (e) { statuses = []; }
        }

        const entry = {
          jobId,
          videoName,
          status: "processing",
        };
        statuses.push(entry);
        fs.writeFileSync(statusFile, JSON.stringify(statuses, null, 2), "utf8");
      } catch (e) {
        console.error("Failed to write status entry:", e);
      }
      return res.status(202).json({ jobId });
    } catch (err) {
      console.error("Failed to start processing job:", err);
      return res.status(500).json({ error: "Failed to start job" });
    }
  },

  // GET /api/status/:jobId
  getStatus(req, res) {
    const { jobId } = req.params;
    if (!jobId) {
      return res.status(404).json({ error: "No job ID provided" });
    }

    try {
      // Use an absolute path (based on current working dir) so behavior is stable
      const statusFile = path.resolve(process.cwd(), "src", "processed", "status.json");

      // If we have a status file, try to find the job record
      if (fs.existsSync(statusFile)) {
        try {
          const raw = fs.readFileSync(statusFile, "utf8");
          const list = JSON.parse(raw || "[]");
          const rec = list.find(r => String(r.jobId) === String(jobId));
          if (rec) {
            // If CSV exists, augment the record with result path
            const csvPath = path.resolve(process.cwd(), "output", `${jobId}.csv`);
            if (fs.existsSync(csvPath)) {
              rec.status = rec.status === 'processing' ? 'complete' : rec.status;
              rec.result = csvPath;
            }
            return res.status(200).json(rec);
          }
        } catch (e) {
          console.error("Failed to parse status.json", e);
          // If parsing fails, continue to CSV fallback below instead of hanging
        }
      }

      // Fallback: check for an output CSV with the jobId name (handles jobs
      // that were not written into status.json for any reason).
      const fallbackCsv = path.resolve(process.cwd(), "output", `${jobId}.csv`);
      if (fs.existsSync(fallbackCsv)) {
        return res.status(200).json({ jobId, status: "complete", result: fallbackCsv });
      }

      // If we reach here, no record or CSV found — return 404 (don't hang)
      return res.status(404).json({ error: `Job not found: ${jobId}` });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }
};
  