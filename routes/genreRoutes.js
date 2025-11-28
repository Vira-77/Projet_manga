const express = require('express');
const router = express.Router();

// Importation des contrôleurs et middlewares
const genreController = require('../controllers/genreController');
const { verifyToken, authorizeRoles } = require('../middlewares/authMiddleware'); 

// ==========================
//   ROUTES PUBLIQUES
// ==========================

// Récupérer tous les genres
router.get('/', genreController.getAllGenres);

// Récupérer un genre par ID
router.get('/:id', genreController.getGenreById);

// Récupérer un genre par nom
router.get('/name/:name', genreController.getGenreByName);


// ==========================
//   ROUTES PROTÉGÉES - ADMIN SEULEMENT
// ==========================

// Créer un genre
router.post(
    '/', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    genreController.createGenre
);

// Mettre à jour un genre par ID
router.put(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    genreController.updateGenre
);

// Supprimer un genre par ID
router.delete(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin'), 
    genreController.deleteGenre
);

// ==========================
//   GESTION DES MANGAS DANS LES GENRES
// ==========================

// Ajouter un manga à un genre
router.post(
    '/:genreId/mangas/:mangaId', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    genreController.addMangaToGenre
);

// Retirer un manga d'un genre
router.delete(
    '/:genreId/mangas/:mangaId', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    genreController.removeMangaFromGenre
);

module.exports = router;