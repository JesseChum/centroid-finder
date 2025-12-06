import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import { exec, spawn } from "child_process";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const PROJECT_VIDEOS = "/videos";

//Debugging lines
console.log("VIDEOS DIRECTORY:", process.env.VIDEOS_DIR);
console.log("RESOLVED PROJECT_VIDEOS:", PROJECT_VIDEOS);
console.log("CURRENT WORKING DIR:", process.cwd());

export const videoController = {

    // Get All Videos is used by the API
    // What it does is receieve a path from the enviornment, expected to be Docker,
    // and scans over every file directory. It then returns an array of strings.
    // it expects mp4 files, and tacks on '.mp4' to the end of the string before
    // adding it to the array.
    // Currently we only handle mp4 as the java format is set up to only 
    // handle mp4. 
    getAllVideos(req, res) {
    try {
        const videosPath = process.env.VIDEOS_DIR || PROJECT_VIDEOS;
        console.log("[getAllVideos] Using videos path:", videosPath);
        // console.log(`[getAllVideos] VIDEOS_DIRECTORY: "${videosPath}"`);
        // console.log(`[getAllVideos] Directory exists: ${fs.existsSync(videosPath)}`);
        
        if (!fs.existsSync(videosPath)) {
            return res.status(404).json({ error: "Videos directory not found", path: videosPath });
        }
        
        const files = fs.readdirSync(videosPath);
        console.log(`[getAllVideos] All files found:`, files);
        
        const videoNames = files
        .filter(f => f.endsWith(".mp4"))
        .map(f => f.replace(".mp4", ""));

        console.log(`[getAllVideos] Filtered .mp4 files:`, videoNames);
        return res.status(200).json(videoNames);
    } catch (err) {
        console.error(`[getAllVideos] Error:`, err);
        return res.status(500).json({ error: err.message });
    }
    },
    
  // We recieve a video name from the API as a parameter.
  // if it is valid, we attempt to generate a thumbnail for the video.
  // it does so by running a command in a console that forces the JAR to 
  // grab the first frame of the video.
  // error handling will cause it to be skipped over, but not fail the entire call.
  // NOTE: most due for a rework. The Binarization feature is not added
  processVideo(req, res) {
      const { videoName } = req.params;

      if (!videoName) {
        return res.status(404).json({ error: "No video name provided" });
      }

      const videoPath = path.join(process.env.VIDEOS_DIR || PROJECT_VIDEOS, `${videoName}.mp4`);
      if (!fs.existsSync(videoPath)) {
        return res.status(404).json({ error: "Video not found" });
      }
      const jarPath = process.env.JAR_PATH;
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
    if (!fs.existsSync(videoPath)) {
      return res.status(404).json({ error: "Video not found" });
    }
    const jarPath = process.env.JAR_PATH;
    const outputCsv = path.join(process.env.RESULTS_DIR, `${videoName}.csv`);
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
  // It does this by following the path of the status, then depending on the state of the CSV,
  // outputs a status.
  // If there is a CSV, then there is a complete job.
  // If there is no CSV, then it handles checking for if it ever began or recieved an error.
  // Eventually, if all fails, the assumption is that a job never began.
  // NOTE: Rework path to not be static.
  getStatus(req, res) {
  try {
    const resultsPath = process.env.RESULTS_DIR || "/results";

    const files = fs.readdirSync(resultsPath);

    const csvFiles = files.filter(f => f.endsWith(".csv"));

    const results = csvFiles.map(filename => {
      const fullPath = path.join(resultsPath, filename);
      const raw = fs.readFileSync(fullPath, "utf8");

      return {
        name: filename.replace(".csv", ""),
        csv: raw.split("\n").filter(line => line.trim() !== ""),
      };
    });

    return res.status(200).json(results);

  } catch (error) {
    console.error("Error loading CSV results:", error);
    return res.status(200).json([{error: error.message}]);
  }
}

};
