import { videoRepo } from "../repo/videos.repo.js";

export const videoController = {
    getAllVideos: async (req, res) => {
        try {
            const videos = await videoRepo.findAll();
            res.json(videos);
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    },

    getVideoById: async (req, res) => {
        try {
            const video = await videoRepo.findById(req.params.id);
            if (!video) {
                return res.status(404).json({ error: "Video not found" });
            }
            res.json(video);
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    },

    createVideo: async (req, res) => {
        try {
            const newVideo = await videoRepo.create(req.body);
            res.status(201).json(newVideo);
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    },

    updateVideo: async (req, res) => {
        try {
            const updatedVideo = await videoRepo.update(req.params.id, req.body);
            if (!updatedVideo) {
                return res.status(404).json({ error: "Video not found" });
            }
            res.json(updatedVideo);
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    },

    deleteVideo: async (req, res) => {
        try {
            const result = await videoRepo.delete(req.params.id);
            if (!result) {
                return res.status(404).json({ error: "Video not found" });
            }
            res.status(204).send();
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    }
};