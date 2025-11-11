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
    const thumbCommand = `java -cp "${jarPath}" io.jessechum.centroidfinder.ThumbNailGenerator "${videoPath}"`;
    exec(thumbCommand, (thumbError, thumbOutput, thumbStderr) => {
      if (thumbError) {
        console.warn("Thumbnail generation failed", thumbStderr);
      }
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
      const csvPath = path.join("..", "output", `${jobId}.csv`);
      if (!fs.existsSync(csvPath)) {
        return res.status(200).json({ status: "processing" });
      }

      const csvData = fs.readFileSync(csvPath, "utf8");
      return res.status(200).json({
        status: "complete",
        data: csvData
      });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  }
};
  