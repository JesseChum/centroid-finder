import fs from 'fs/promises';
import sequelize from '../db/connections.js'
import schema from '../models/products.schema.js'

const videos = await fs.readFile('./src/seed/videos.json');
const jsonVideos = JSON.parse(videos);

for(const video of jsonVideos){
    await schema.create(video);
}
console.log(`Inserted ${jsonVideos.length} video records`);