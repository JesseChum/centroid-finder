import express from "express";
import { videoController } from "../controllers/api.controller.js";

export const apiRouter = express.Router();

// Define routes
apiRouter.get("/api/videos", videoController.getAllVideos);
apiRouter.get("/api/thumbnail/:filename", videoController.getThumbnail);
apiRouter.post("/api/process/:filename", videoController.startJob);
apiRouter.post("/api/process/:jobId/status", videoController.getStatus);