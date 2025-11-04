import sequelize from '../db/connections.js';
import { DataTypes } from 'sequelize';


const schema = sequelize.define('product', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
    },
});


export default schema;