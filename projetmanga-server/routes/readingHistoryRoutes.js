const express = require('express');
const router = express.Router();
const readingHistoryController = require('../controllers/readingHistoryController');
const { verifyToken } = require('../middlewares/authMiddleware');

// Mettre à jour l'historique de lecture
router.put('/', verifyToken, readingHistoryController.updateReadingHistory);

// Récupérer l'historique de lecture de l'utilisateur
router.get('/', verifyToken, readingHistoryController.getReadingHistory);

// Récupérer l'historique d'un manga spécifique
router.get('/:mangaId', verifyToken, readingHistoryController.getMangaReadingHistory);

// Supprimer un élément de l'historique
router.delete('/:mangaId', verifyToken, readingHistoryController.deleteReadingHistory);

module.exports = router;

