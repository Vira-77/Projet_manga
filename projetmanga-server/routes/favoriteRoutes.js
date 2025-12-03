const express = require('express');
const router = express.Router();
const favoriteController = require('../controllers/favoriteController');
const { verifyToken } = require('../middlewares/authMiddleware');

// ▶️ Tous les favoris d’un user
router.get('/', verifyToken, favoriteController.getFavorites);

// ➕ Ajouter aux favoris
router.post('/', verifyToken, favoriteController.addFavorite);

// ❌ Retirer un favori
router.delete('/:mangaId', verifyToken, favoriteController.removeFavorite);

module.exports = router;
