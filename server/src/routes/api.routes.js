import express from "express";
import { videoController } from "../controllers/api.controller.js";

export const apiRouter = express.Router();

// Define routes
apiRouter.get("/api/videos", videoController.getAllVideos);
apiRouter.get("/api/videos/:id", videoController.getVideoById);
apiRouter.post("/api/videos", videoController.createVideo);
apiRouter.put("/api/videos/:id", videoController.updateVideo);
apiRouter.delete("/api/videos/:id", videoController.deleteVideo);