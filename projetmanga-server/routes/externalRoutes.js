const express = require('express');
const router = express.Router();
const externalController = require('../controllers/externalController');

// 🔍 Recherche d’un manga (public)
router.get('/search', externalController.searchManga);

// 🔝 Top mangas (public)
router.get('/top', externalController.getTopMangas);

// 📘 Manga par ID Jikan (public)
router.get('/manga/:id', externalController.getMangaById);

router.get('/genres', externalController.getAllGenres);

router.get('/genres/:id/manga', externalController.getMangaByGenre);

router.get('/genres/search/:name', externalController.searchGenreAndMangas);

module.exports = router;
