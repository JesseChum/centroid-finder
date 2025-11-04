import schema from '../models/videos.schema.js';

export const videoRepo = {
    findAll: async () => {
        try {
            return await schema.findAll();
        } catch (error) {
            throw new Error(`Error fetching videos: ${error.message}`);
        }
    },

    findById: async (id) => {
        try {
            return await schema.findByPk(id);
        } catch (error) {
            throw new Error(`Error fetching video by id: ${error.message}`);
        }
    },

    create: async (videoData) => {
        try {
            return await schema.create(videoData);
        } catch (error) {
            throw new Error(`Error creating video: ${error.message}`);
        }
    },

    update: async (id, videoData) => {
        try {
            const video = await schema.findByPk(id);
            if (!video) return null;
            return await video.update(videoData);
        } catch (error) {
            throw new Error(`Error updating video: ${error.message}`);
        }
    },

    delete: async (id) => {
        try {
            const video = await schema.findByPk(id);
            if (!video) return false;
            await video.destroy();
            return true;
        } catch (error) {
            throw new Error(`Error deleting video: ${error.message}`);
        }
    }
};