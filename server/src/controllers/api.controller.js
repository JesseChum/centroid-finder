import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import { exec, spawn } from "child_process";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const PROJECT_VIDEOS = "/videos";

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
        const videosPath = process.env.VIDEOS_DIRECTORY || PROJECT_VIDEOS;
        
        if (!fs.existsSync(videosPath)) {
            return res.status(404).json({ error: "Videos directory not found", path: videosPath });
        }
        
        const files = fs.readdirSync(videosPath);
        const videoNames = files
        .filter(f => f.endsWith(".mp4"))
        .map(f => f.replace(".mp4", ""));

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

      const videoPath = path.join(process.env.VIDEOS_DIRECTORY || PROJECT_VIDEOS, `${videoName}.mp4`);
      if (!fs.existsSync(videoPath)) {
        return res.status(404).json({ error: "Video not found" });
      }
      const jarPath = process.env.JAR_PATH;
      const outputDir = path.join(process.cwd(), "output");
      const thumbnailPath = path.join(outputDir, `${videoName}.png`);
      
      const thumbCommand = `java -cp "${jarPath}" io.JesseChum.centroidfinder.SimpleThumbnailGenerator "${videoPath}"`;
      exec(thumbCommand, (thumbError, thumbOutput, thumbStderr) => {
        if (thumbError) {
          console.error("Thumbnail generation failed:", thumbStderr || thumbError.message);
          return res.status(500).json({ 
            error: "Thumbnail generation failed", 
            details: thumbStderr || thumbError.message 
          });
        }
        
        // Check if thumbnail was created
        if (!fs.existsSync(thumbnailPath)) {
          return res.status(500).json({ error: "Thumbnail file not found after generation" });
        }
        
        // Send the PNG file
        res.setHeader("Content-Type", "image/png");
        res.sendFile(thumbnailPath);
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
    const videoPath = path.join(process.env.VIDEOS_DIRECTORY || PROJECT_VIDEOS, `${videoName}.mp4`);
    if (!fs.existsSync(videoPath)) {
      return res.status(404).json({ error: "Video not found" });
    }
    const jarPath = process.env.JAR_PATH;
    const resultsDir = process.env.RESULTS_DIRECTORY || "/results";
    
    // Create results directory if it doesn't exist
    if (!fs.existsSync(resultsDir)) {
      fs.mkdirSync(resultsDir, { recursive: true });
    }
    
    const outputCsv = path.join(resultsDir, `${videoName}.csv`);
    const targetColor = color;
    const thr = threshold

    const jobId = `${videoName}-${Date.now()}`;

    console.log(`Starting job ${jobId}: ${videoName}`);

    // Build java args and spawn detached process so we return immediately.
    const args = ["-cp", jarPath, "io.JesseChum.centroidfinder.VideoApp", videoPath, outputCsv, targetColor, thr];
    
    try {
      const child = spawn("java", args, { 
        detached: true, 
        stdio: ["ignore", "pipe", "pipe"]
      });
      
      child.stdout.on("data", (data) => {
        console.log(`[${jobId}]:`, data.toString().trim());
      });
      
      child.stderr.on("data", (data) => {
        console.error(`[${jobId} ERROR]:`, data.toString().trim());
      });
      
      child.on("error", (error) => {
        console.error(`Job ${jobId} spawn error:`, error.message);
      });
      
      child.on("exit", (code) => {
        console.log(`Job ${jobId} exited with code ${code}`);
      });
      
      child.unref();

       try {
        const statusDir = path.join("src", "processed");
        const statusFile = path.join(statusDir, "status.json");
        
        // Create directory if it doesn't exist
        if (!fs.existsSync(statusDir)) {
          fs.mkdirSync(statusDir, { recursive: true });
        }
        
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
        console.error("Failed to write status:", e.message);
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
},

  //Delete csv output
  deleteResult(req, res) {
  try {
    // 1. Get `name` from route param /results/:name
    const { name } = req.params;
    console.log("[deleteResult] got param name:", name);

    if (!name) {
      return res.status(400).json({ error: "No name provided" });
    }

    // 2. Use the same env var as the rest of your app / Dockerfile
    const resultsPath = process.env.RESULTS_DIRECTORY || "/results";
    const filePath = path.join(resultsPath, `${name}.csv`);

    // 3. Make sure the file exists
    if (!fs.existsSync(filePath)) {
      return res.status(404).json({ error: "CSV file not found" });
    }

    // 4. Delete it
    fs.unlinkSync(filePath);
    console.log("[deleteResult] deleted:", filePath);

    return res.status(200).json({ message: `Deleted ${name}.csv` });
  } catch (err) {
    console.error("Delete error:", err);
    return res.status(500).json({ error: err.message });
  }
}
};
