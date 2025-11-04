import express from "express";
import { videoController } from "../controllers/api.controller.js";

export const apiRouter = express.Router();

// Define routes
apiRouter.get("/api/videos", videoController.getAllVideos);
apiRouter.get("/api/thumbnail/:filename", videoController.getVideoById);
apiRouter.post("/api/process/:filename", videoController.createVideo);
apiRouter.post("/api/process/:jobId/status", videoController.updateVideo);