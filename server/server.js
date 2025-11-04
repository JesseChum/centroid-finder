import express from "express";
import { apiRouter } from "./src/routes/api.routes.js";

const app = express();
const PORT = 3000;

app.set("view engine", "pug");
app.set("views", "src/views");
app.use(express.urlencoded({urlencoded: true}));
app.use(express.json());
app.use(express.static("./src/public"));
app.use("/", apiRouter);

app.listen(PORT, console.log(`http://localhost:${PORT}`));