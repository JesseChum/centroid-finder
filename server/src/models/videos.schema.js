import sequelize from '../db/connections.js';
import { DataTypes } from 'sequelize';


const schema = sequelize.define('product', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
    },
    processed: {
        type: DataTypes.BOOLEAN,
        allowNull: false
    },
    name: {
        type: DataTypes.STRING,
        allowNull: true
    },
});


export default schema;