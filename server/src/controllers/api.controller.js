export const videoController = {
    getAllVideos: async (req, res) => {
        
    },

    getVideoById: async (req, res) => {
        try {
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
            res.status(201).json(newVideo);
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    },

    updateVideo: async (req, res) => {
        try {
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
            if (!result) {
                return res.status(404).json({ error: "Video not found" });
            }
            res.status(204).send();
        } catch (error) {
            res.status(500).json({ error: error.message });
        }
    }
};