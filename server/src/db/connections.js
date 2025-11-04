import { Sequelize } from 'sequelize';
import dotenv from 'dotenv';

//get the credentials from the .env file
dotenv.config();
const { DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD, DB_DATABASE } = process.env;

//configure the connection
const sequelize = new Sequelize(DB_DATABASE, DB_USERNAME, DB_PASSWORD, {
    host: DB_HOST,
    port: DB_PORT,
    dialect: 'mysql',
    logging: null //q => console.log(`Sequelize query: ${q}`)
})

//start the connection
try {
    await sequelize.authenticate();
    console.log("Connected to MySQL DB!");
} catch (err) {
    console.log("Can't connect to DB!");
    console.log(err);
}

export default sequelize;