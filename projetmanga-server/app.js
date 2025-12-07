// ==========================
//   Import des dépendances
// ==========================
const express = require('express');
const http = require('http');
const cors = require('cors');
const mongoose = require('mongoose');
const path = require('path');


// ==========================
//   Initialisation Express
// ==========================
const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));


app.use('/uploads', express.static('public/uploads'));


// ==========================
//   Connexion à MongoDB
// ==========================
const connectDB = require('./db/db');
connectDB();


// ==========================
//   Import des middlewares et routes
// ==========================
//const { verifyToken, authorizeRoles } = require('./middlewares/authMiddleware');

const authRouter = require('./routes/auth'); // Routes d'authentification
app.use('/auth', authRouter);

const userRoutes = require('./routes/userRoutes');
const mangaRoutes = require('./routes/mangaRoutes');
const chapterRoutes = require('./routes/chapterRoutes');
const genreRoutes = require('./routes/genreRoutes');
const storeRoutes = require('./routes/storeRoutes');
const externalRoutes = require('./routes/externalRoutes');
const favoriteRoutes = require('./routes/favoriteRoutes');
const readingHistoryRoutes = require('./routes/readingHistoryRoutes');
const socketRoutes = require('./routes/socketRoutes');
const aiRoutes = require('./routes/aiRoutes');

app.use('/users', userRoutes);
app.use('/mangas', mangaRoutes);
app.use('/chapters', chapterRoutes);
app.use('/genres', genreRoutes);
app.use('/stores', storeRoutes);
app.use('/external', externalRoutes);
app.use('/favorites', favoriteRoutes);
app.use('/reading-history', readingHistoryRoutes);
app.use('/socket', socketRoutes);
app.use("/ai", aiRoutes);
// ==========================
//   Socket.io & serveur HTTP
// ==========================
const { initSocket } = require('./websocket/socket');



const PORT = process.env.PORT || 3000;
const server = http.createServer(app);

// Initialisation de Socket.io avec rooms
initSocket(server);

server.listen(PORT, () => {
  console.log(`Serveur Manga en ligne sur http://localhost:${PORT}`);
});
