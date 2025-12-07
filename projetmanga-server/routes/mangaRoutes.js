const express = require('express');
const router = express.Router();

// Importation des contrôleurs et middlewares
const mangaController = require('../controllers/mangaController');
const { verifyToken, authorizeRoles } = require('../middlewares/authMiddleware'); 



// ==========================
//   ROUTES PUBLIQUES
// ==========================

// Récupérer tous les mangas
router.get('/', mangaController.getAllMangas);

// Récupérer un manga par nom
router.get('/name/:name', mangaController.getMangaByName);

// Récupérer les mangas d'un genre
router.get('/genre/:genreId', mangaController.getMangasByGenre);

// Récupérer les mangas d'un auteur
router.get('/author/:author', mangaController.getMangasByAuthor);

// IMPORTANT: La route générique /:id doit être EN DERNIER pour éviter les conflits
// Récupérer un manga par ID
router.get('/:id', mangaController.getMangaById);

// ==========================
//   ROUTES PROTÉGÉES - ADMIN MANGA ET ADMIN
// ==========================

// Créer un manga
router.post(
    '/', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    mangaController.createManga
);

// Mettre à jour un manga par ID
router.put(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    mangaController.updateManga
);

// Supprimer un manga via POST (pour contourner les soucis de DELETE côté client)
router.post('/:id/delete',
    verifyToken,
    authorizeRoles('admin', 'admin_manga'),
    mangaController.deleteManga
);


// Supprimer un manga par ID
router.delete(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'),
    mangaController.deleteManga
);

module.exports = router;