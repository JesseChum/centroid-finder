import express from "express";
import { apiRouter } from "./src/routes/api.routes.js";
import cors from "cors";

const app = express();
const PORT = 3000;

app.use(cors());

app.set("view engine", "pug");
app.set("views", "src/views");
app.use(express.urlencoded({ extended: true}));
app.use(express.json());
app.use(express.static("./src/public"));
app.use("/api", apiRouter);

app.listen(PORT, console.log(`http://localhost:${PORT}`));