const express = require('express');
const router = express.Router();
const favoriteController = require('../controllers/favoriteController');
const { verifyToken } = require('../middlewares/authMiddleware');

// ▶️ Tous les favoris d'un user (avec token, utilise req.user.id)
router.get('/', verifyToken, favoriteController.getFavorites);

// ▶️ Tous les favoris d'un user (avec userId dans l'URL pour compatibilité)
router.get('/:userId', verifyToken, favoriteController.getFavoritesByUserId);

// ➕ Ajouter aux favoris
router.post('/', verifyToken, favoriteController.addFavorite);

// ❌ Retirer un favori
router.delete('/:mangaId', verifyToken, favoriteController.removeFavorite);

module.exports = router;
