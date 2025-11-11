import express from "express";
import { videoController } from "../controllers/api.controller.js";

export const apiRouter = express.Router();

// Define routes
apiRouter.get("/videos", videoController.getAllVideos);

apiRouter.get("/thumbnail/:videoName", videoController.processVideo);

// start process - starts the actual job
apiRouter.post("/process/:videoName", videoController.startProcess);

//original 
// apiRouter.post("/process/:jobId/status", videoController.getStatus);
//changed
apiRouter.get("/status/:jobId", videoController.getStatus);