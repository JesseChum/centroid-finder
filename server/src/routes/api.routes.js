import { Router } from 'express';

const apiRouter = Router();

apiRouter.get("/", getVideos);

export default apiRouter;