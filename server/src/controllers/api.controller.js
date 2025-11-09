import fs from "fs";
import path from "path";
import { exec } from "child_process";

export const videoController = {
//      getAllVideos {
//     // try to grab json file
//     // if have json
//         // iterate through json file
//         // add all video names into a list
//         // return list
//     // else
//         // return 404 of no list
// }
    getAllVideos(req, res){
        try {
            const dataPath = path.join("data", "videos.json");
              if (!fs.existsSync(dataPath)) {
                return res.status(404).json({ error: "Video list not found" });
        }
         const videos = JSON.parse(fs.readFileSync(dataPath));
      const names = videos.map(v => v.name);
      res.status(200).json(names);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
    },
    
  processVideo(req, res) {
    const {videoName } = req.params;

    //Step 1: Check for video name
    if (!videoName) {
      res.status(404).json({ error: "No video name provided" });
      return;
    }

    //Step 2: check if video file exists
    const videoPath = path.join("videos", `${videoName}.mp4`);
     if (!fs.existsSync(videoPath)) {
      res.status(404).json({ error: "Video not found" });
      return;
  }
   //Step 3: Try to make thumbnail
   //need to create command still. putting in a random example for now so I dont see an annoying error
    exec(`java -jar videoProcessor.jar thumbnail ${videoPath}`, (thumbError, thumbOutput) => {
      if (thumbError) {
        console.log("Error making thumbnail:", thumbError);
        res.status(500).json({ error: "Could not make thumbnail" });
        return;
      }

      const thumbnail = thumbOutput.trim();

      //Step 4: Start job next
      exec(`java -jar videoProcessor.jar start ${videoName}`, (jobError, jobOutput) => {
        if (jobError) {
          console.log("Error starting job:", jobError);
          res.status(400).json({ error: "Could not start job" });
          return;
        }

        const jobId = jobOutput.trim();

      //Step 5: If both worked, send success reponse
       res.status(200).json({
          message: "Video processed successfully",
          video: videoName,
          thumbnail: thumbnail,
          jobId: jobId
        });
      });
    });
  },

//     * getStatus
//     // if jobId does not exist
//         // return no jobid status
//     // if jobId exists
//         // if progress is error
//             // return error status
//         // if progress is processing
//             // return status of process
//         // if has csv
//             // return csv

    getStatus(req, res) {
        const { jobId } = req.params;
        if (!jobId) {
            return res.status(404).json({ error: "No job ID provided" });
        }

        try {
            // Check job status using jar
            exec(`java -jar videoProcessor.jar status ${jobId}`, (err, stdout) => {
                if (err) {
                    return res.status(500).json({ error: "Error checking job status" });
                }

                const status = stdout.trim();
                
                // Check if job resulted in error
                if (status === "error") {
                    return res.status(500).json({ status: "error" });
                }
                
                // Check if job is still processing
                if (status === "processing") {
                    return res.status(200).json({ status: "processing" });
                }

                // If job is complete, check for CSV file
                const csvPath = path.join("output", `${jobId}.csv`);
                if (fs.existsSync(csvPath)) {
                    const csvData = fs.readFileSync(csvPath, 'utf8');
                    return res.status(200).json({ 
                        status: "complete",
                        data: csvData
                    });
                }

                // If we get here, something unexpected happened
                return res.status(500).json({ error: "Unexpected job state" });
            });
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    }
    
};