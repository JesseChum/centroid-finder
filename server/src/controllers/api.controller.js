import fs from "fs";
import path from "path";
import { exec } from "child_process";

export const videoController = {
    getAllVideos(req, res){
        try {
            const dataPath = path.join("src", "seed", "videos.json");
              if (!fs.existsSync(dataPath)) {
                return res.status(404).json({ error: "Video list not found" });
        }
         const videos = JSON.parse(fs.readFileSync(dataPath, "utf8"));
      const names = videos.map(v => v.name);
      res.status(200).json(names);
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
    const videoPath = path.join("..", "src", "main", "resources", `${videoName}.mp4`);
    if (!fs.existsSync(videoPath)) {
      return res.status(404).json({ error: "Video not found" });
    }

    const jarPath = path.join("..", "processor", "target", "centroid-finder-1.0-SNAPSHOT.jar");
    const outputCsv = path.join("..", "output", `${videoName}.csv`);
    const targetColor = "255,0,0";
    const threshold = "30";
    const jobId = videoName;

    // Step 3: Generate thumbnail
    const thumbCommand = `java -cp "${jarPath}" io.jessechum.centroidfinder.ThumbNailGenerator "${videoPath}"`;
    exec(thumbCommand, (thumbError, thumbOutput, thumbStderr) => {
      if (thumbError) {
        console.error("Error making thumbnail:", thumbStderr);
        return res.status(500).json({ error: "Could not make thumbnail" });
      }

      // Step 4: Start job
      const processCommand = `java -cp "${jarPath}" io.jessechum.centroidfinder.VideoApp "${videoPath}" "${outputCsv}" "${targetColor}" "${threshold}"`;
      exec(processCommand, (jobError, jobOutput, jobStderr) => {
        if (jobError) {
          console.error("Error starting job:", jobStderr);
          return res.status(400).json({ error: "Could not start job" });
        }

        // Step 5: Return success
        res.status(200).json({
          message: "Video processed successfully",
          video: videoName,
          jobId: jobId,
          thumbnail: thumbOutput.trim()
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