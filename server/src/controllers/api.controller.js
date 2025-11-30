import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import { exec, spawn } from "child_process";

//Debugging lines
console.log("VIDEOS DIRECTORY:", process.env.VIDEOS_DIRECTORY);
console.log("RESOLVED PROJECT_VIDEOS:", PROJECT_VIDEOS);
console.log("CURRENT WORKING DIR:", process.cwd());

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const PROJECT_VIDEOS = path.resolve(__dirname, "../../videos");

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
        console.log(`[getAllVideos] VIDEOS_DIRECTORY: "${videosPath}"`);
        console.log(`[getAllVideos] Directory exists: ${fs.existsSync(videosPath)}`);
        
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

      const videoPath = path.join(process.env.VIDEOS_DIRECTORY, `${videoName}.mp4`);
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
    const outputCsv = path.join(process.env.RESULTS_DIRECTORY, `${videoName}.csv`);
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
  // recieves a jobID through a parameter, and returns the status of the job.
  // It does this by following the path of the status, then depending on the state of the CSV,
  // outputs a status.
  // If there is a CSV, then there is a complete job.
  // If there is no CSV, then it handles checking for if it ever began or recieved an error.
  // Eventually, if all fails, the assumption is that a job never began.
  // NOTE: Rework path to not be static.
  getStatus(req, res) {
    const { jobId } = req.params;
    console.log(`[getStatus] Received jobId: "${jobId}" (type: ${typeof jobId})`);
    
    if (!jobId) {
      return res.status(404).json({ error: "No job ID provided" });
    }

    try {
      // Use an absolute path (based on current working dir) so behavior is stable
      const statusFile = path.resolve(process.cwd(), "src", "processed", "status.json");
      console.log(`[getStatus] Looking for status file at: ${statusFile}`);

      // If we have a status file, try to find the job record
      if (fs.existsSync(statusFile)) {
        console.log(`[getStatus] Status file exists, reading...`);
        try {
          const raw = fs.readFileSync(statusFile, "utf8");
          const list = JSON.parse(raw || "[]");
          console.log(`[getStatus] Parsed ${list.length} entries from status.json`);
          console.log(`[getStatus] Entries in file:`, list.map(r => ({ jobId: r.jobId, type: typeof r.jobId })));
          
          const rec = list.find(r => {
            const match = String(r.jobId) === String(jobId);
            console.log(`[getStatus] Comparing "${r.jobId}" (${typeof r.jobId}) === "${jobId}" (${typeof jobId}) => ${match}`);
            return match;
          });
          
          if (rec) {
            console.log(`[getStatus] Found record for jobId: ${jobId}`, rec);
            // If CSV exists, augment the record with result path
            const csvPath = path.resolve(process.cwd(), "output", `${jobId}.csv`);
            if (fs.existsSync(csvPath)) {
              rec.status = rec.status === 'processing' ? 'complete' : rec.status;
              rec.result = csvPath;
            }
            return res.status(200).json(rec);
          } else {
            console.log(`[getStatus] No matching record found in status.json for jobId: ${jobId}`);
          }
        } catch (e) {
          console.error(`[getStatus] Failed to parse status.json:`, e);
        }
      } else {
        console.log(`[getStatus] Status file does not exist at: ${statusFile}`);
      }

      console.log(`[getStatus] Checking fallback CSV path for jobId: ${jobId}`);
      const fallbackCsv = path.resolve(process.cwd(), "output", `${jobId}.csv`);
      console.log(`[getStatus] Fallback CSV path: ${fallbackCsv}`);
      if (fs.existsSync(fallbackCsv)) {
        console.log(`[getStatus] Found CSV file at fallback path, returning complete status`);
        return res.status(200).json({ jobId, status: "complete", result: fallbackCsv });
      }
      
      // If we reach here, no record or CSV found — return 404 (don't hang)
      console.log(`[getStatus] No record or CSV found for jobId: ${jobId}, returning 404`);
      return res.status(404).json({ error: `Job not found: ${jobId}` });
    } catch (error) {
      console.error(`[getStatus] Unexpected error:`, error);
      res.status(500).json({ error: error.message });
    }
  }
};
