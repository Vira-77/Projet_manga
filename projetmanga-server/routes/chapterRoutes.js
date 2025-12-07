const express = require('express');
const router = express.Router();

// Importation des contrôleurs et middlewares
const chapterController = require('../controllers/chapterController');
const { verifyToken, authorizeRoles } = require('../middlewares/authMiddleware'); 
const { uploadChapterPage } = require('../middlewares/upload'); // ✅ AJOUTER

// ==========================
//   ROUTES PUBLIQUES
// ==========================

// Récupérer tous les chapitres
router.get('/', chapterController.getAllChapters);

// Récupérer les chapitres d'un manga
router.get('/manga/:mangaId', chapterController.getChaptersByManga);

// Récupérer les chapitres d'un manga
router.get('/mangaDetail/:mangaId', chapterController.getChaptersByMangaAffichageDetail);

// Récupérer un chapitre par ID (avec pages par défaut)
router.get('/:id', chapterController.getChapterById);

// ==========================
//   ROUTES PROTÉGÉES - ADMIN MANGA ET ADMIN
// ==========================

// Créer un chapitre
router.post(
    '/', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    chapterController.createChapter
);

// Mettre à jour un chapitre par ID
router.put(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    chapterController.updateChapter
);

// Supprimer un chapitre par ID
router.delete(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin'), 
    chapterController.deleteChapter
);

// ==========================
//   GESTION DES PAGES
// ==========================

// ✅ Ajouter une page à un chapitre (AVEC UPLOAD)
router.post(
    '/:chapterId/pages', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'),
    uploadChapterPage.single('image'), // ✅ AJOUTER le middleware multer
    chapterController.addPageToChapter
);

// Supprimer une page d'un chapitre
router.delete(
    '/:chapterId/pages/:pageNumber', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    chapterController.removePageFromChapter
);

module.exports = router;