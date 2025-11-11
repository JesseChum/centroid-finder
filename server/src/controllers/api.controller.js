import fs from "fs";
import path from "path";
import { exec } from "child_process";

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

    const jarPath = path.join("..", "processor", "target", "centroid-finder-1.0-SNAPSHOT.jar");
    const outputCsv = path.join("..", "output", `${videoName}.csv`);
    const targetColor = "255,0,0";
    const threshold = "30";
    const jobId = videoName;

    // Step 3: Generate thumbnail (if possible). If thumbnail generation fails (for
    // example missing JavaFX runtime), continue and only log the error — do not
    // abort the processing job.
    const thumbCommand = `java -cp "${jarPath}" io.jessechum.centroidfinder.ThumbNailGenerator "${videoPath}"`;
    exec(thumbCommand, (thumbError, thumbOutput, thumbStderr) => {
      if (thumbError) {
        // Log the thumbnail error but continue processing the video. This avoids
        // failing the whole request when the environment lacks JavaFX.
        console.warn("Thumbnail generation failed, skipping thumbnail:", thumbStderr);
      }

      // Step 4: Start job (always run regardless of thumbnail result)
      const processCommand = `java -cp "${jarPath}" io.jessechum.centroidfinder.VideoApp "${videoPath}" "${outputCsv}" "${targetColor}" "${threshold}"`;
      exec(processCommand, (jobError, jobOutput, jobStderr) => {
        if (jobError) {
          console.error("Error starting job:", jobStderr);
          return res.status(400).json({ error: "Could not start job" });
        }

        // Step 5: Return success. Use thumbnail output when available, otherwise
        // return null for thumbnail.
        const thumbnail = thumbOutput && typeof thumbOutput === 'string' ? thumbOutput.trim() : null;
        res.status(200).json({
          message: "Video processed successfully",
          video: videoName,
          jobId: jobId,
          thumbnail: thumbnail
        });
      });
    });
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

// Return a thumbnail for a given video filename (without extension)
  // getThumbnail(req, res) {
  //   const { filename } = req.params;
  //   if (!filename) return res.status(404).json({ error: "No video name" });

  //   try {
  //     const videoPath = path.join("videos", `${filename}.mp4`);
  //     if (!fs.existsSync(videoPath)) {
  //       return res.status(404).json({ error: "Video not found" });
  //     }

  //     exec(`java -jar videoProcessor.jar thumbnail ${videoPath}`, (err, stdout) => {
  //       if (err) return res.status(500).json({ error: "Error generating thumbnail" });
  //       const thumbnail = stdout.trim();
  //       res.status(200).json({ thumbnail });
  //     });
  //   } catch (error) {
  //     res.status(500).json({ error: error.message });
  //   }
  // },

  // Start processing job for a given video filename (without extension)
  // startJob(req, res) {
  //   const { filename } = req.params;
  //   if (!filename) return res.status(404).json({ error: "No video name" });

  //   try {
  //     const videoPath = path.join("videos", `${filename}.mp4`);
  //     if (!fs.existsSync(videoPath)) {
  //       return res.status(404).json({ error: "Video not found" });
  //     }

  //     exec(`java -jar videoProcessor.jar start ${filename}`, (err, stdout) => {
  //       if (err) return res.status(400).json({ error: "Command failed" });
  //       res.status(200).json({ jobId: stdout.trim() });
  //     });
  //   } catch (error) {
  //     res.status(500).json({ error: error.message });
  //   }
  // },