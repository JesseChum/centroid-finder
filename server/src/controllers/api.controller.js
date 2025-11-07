import fs from "fs";
import path from "path";

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
    


//     * getThumbnail
//     // try to grab video
//     // if no video
//     // return 404 of no video
// send video to jar
//    // jar tries to return thumbnail
// if error
//         // return error status
//         // else thumbnail
//         // return status thumbnail
    getThumbnail(req, res) {
    const { videoName } = req.params;
    if (!videoName) return res.status(404).json({ error: "No video name" });

    try {
      const videoPath = path.join("videos", `${videoName}.mp4`);
      if (!fs.existsSync(videoPath)) {
        return res.status(404).json({ error: "Video not found" });
      }
    
      //this line right here is an example jar to get a thumbnail
       exec(`java -jar videoProcessor.jar thumbnail ${videoPath}`, (err, stdout) => {
        if (err) return res.status(500).json({ error: "Error generating thumbnail" });
        res.status(200).json({ thumbnail: stdout });
      });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  },

//     * startJob
//     // try to grab video
//     // if no video
//         // return 404 of no video
//     // else video
//         // send video to jar
//         // if error in command
//             // return bad command status
//         // jar tries to start process
//         // if jar starts process
//             // return jobId status
//         // return job failed status

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
    
}
// };