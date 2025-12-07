const express = require('express');
const router = express.Router();
const socketController = require('../controllers/socketController');
const { verifyToken } = require('../middlewares/authMiddleware');

// Obtenir les rooms Socket.io à rejoindre selon le rôle
router.get('/rooms', verifyToken, socketController.getSocketRooms);

module.exports = router;

