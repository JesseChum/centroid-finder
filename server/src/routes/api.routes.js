import express from "express";
import { videoController } from "../controllers/api.controller.js";

export const apiRouter = express.Router();

// Define routes
apiRouter.get("/videos", videoController.getAllVideos);

//changed the two down here commented out the original 4th router
//commented out original thumbnail
// apiRouter.get("/api/thumbnail/:filename", videoController.getThumbnail);
apiRouter.post("/process/:videoName", videoController.processVideo);

//original 
// apiRouter.post("/process/:jobId/status", videoController.getStatus);
//changed
apiRouter.get("/status/:jobId", videoController.getStatus);