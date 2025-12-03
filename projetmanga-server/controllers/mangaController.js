const mangaService = require('../services/mangaService');

// ==========================
//   CRÉATION D'UN MANGA
// ==========================
exports.createManga = async (req, res) => {
    try {
        const newManga = await mangaService.createManga(req.body);
        
        res.status(201).json({
            message: 'Manga créé avec succès',
            manga: newManga
        });
    } catch (err) {
        console.error('Erreur création manga:', err);
        
        if (err.message.includes('requis') || 
            err.message.includes('existe déjà') || 
            err.message.includes('invalides') ||
            err.message.includes('futur')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION DE TOUS LES MANGAS
// ==========================
exports.getAllMangas = async (req, res) => {
    try {
        const { 
            includeGenres, 
            includeChapters, 
            sortBy, 
            sortOrder, 
            limit, 
            page,
            search
        } = req.query;
        
        const options = {
            includeGenres: includeGenres === 'true',
            includeChapters: includeChapters === 'true',
            sortBy: sortBy || 'nom',
            sortOrder: sortOrder === 'desc' ? -1 : 1,
            limit: limit ? parseInt(limit) : null,
            skip: page ? (parseInt(page) - 1) * (parseInt(limit) || 20) : 0,
            search: search
        };
        
        const mangas = await mangaService.getAllMangas(options);
        
        res.status(200).json({
            message: 'Mangas récupérés avec succès',
            mangas,
            count: mangas.length,
            page: page ? parseInt(page) : 1
        });
    } catch (err) {
        console.error('Erreur récupération mangas:', err);
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION D'UN MANGA PAR ID
// ==========================
exports.getMangaById = async (req, res) => {
    try {
        const { id } = req.params;
        const { includeGenres, includeChapters } = req.query;
        
        const options = {
            includeGenres: includeGenres === 'true',
            includeChapters: includeChapters === 'true'
        };
        
        const manga = await mangaService.getMangaById(id, options);
        
        res.status(200).json({
            message: 'Manga trouvé',
            manga
        });
    } catch (err) {
        console.error('Erreur récupération manga:', err);
        const statusCode = err.message === 'Manga non trouvé' ? 404 : 500;
        res.status(statusCode).json({ 
            message: err.message || 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION D'UN MANGA PAR NOM
// ==========================
exports.getMangaByName = async (req, res) => {
    try {
        const { name } = req.params;
        const { includeGenres, includeChapters } = req.query;
        
        const options = {
            includeGenres: includeGenres === 'true',
            includeChapters: includeChapters === 'true'
        };
        
        const manga = await mangaService.getMangaByName(decodeURIComponent(name), options);
        
        res.status(200).json({
            message: 'Manga trouvé',
            manga
        });
    } catch (err) {
        console.error('Erreur récupération manga par nom:', err);
        const statusCode = err.message === 'Manga non trouvé' ? 404 : 500;
        res.status(statusCode).json({ 
            message: err.message || 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   MISE À JOUR D'UN MANGA
// ==========================
exports.updateManga = async (req, res) => {
    try {
        const { id } = req.params;
        const updatedManga = await mangaService.updateManga(id, req.body);
        
        res.status(200).json({
            message: 'Manga mis à jour avec succès',
            manga: updatedManga
        });
    } catch (err) {
        console.error('Erreur mise à jour manga:', err);
        
        if (err.message === 'Manga non trouvé') {
            return res.status(404).json({ message: err.message });
        }
        
        if (err.message.includes('vide') || 
            err.message.includes('existe déjà') || 
            err.message.includes('invalides') ||
            err.message.includes('futur')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   SUPPRESSION D'UN MANGA
// ==========================
exports.deleteManga = async (req, res) => {
    try {
        const { id } = req.params;
        const deletedManga = await mangaService.deleteManga(id);
        
        res.status(200).json({
            message: 'Manga supprimé avec succès',
            manga: deletedManga
        });
    } catch (err) {
        console.error('Erreur suppression manga:', err);
        
        if (err.message === 'Manga non trouvé') {
            return res.status(404).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   MANGAS PAR GENRE
// ==========================
exports.getMangasByGenre = async (req, res) => {
    try {
        const { genreId } = req.params;
        const { limit, page } = req.query;
        
        const options = {
            limit: limit ? parseInt(limit) : 20,
            skip: page ? (parseInt(page) - 1) * (parseInt(limit) || 20) : 0
        };
        
        const mangas = await mangaService.getMangasByGenre(genreId, options);
        
        res.status(200).json({
            message: 'Mangas du genre récupérés',
            mangas,
            count: mangas.length,
            genreId
        });
    } catch (err) {
        console.error('Erreur récupération mangas par genre:', err);
        
        if (err.message === 'Genre non trouvé') {
            return res.status(404).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   MANGAS PAR AUTEUR
// ==========================
exports.getMangasByAuthor = async (req, res) => {
    try {
        const { author } = req.params;
        const { includeGenres, limit } = req.query;
        
        const options = {
            includeGenres: includeGenres === 'true',
            limit: limit ? parseInt(limit) : 20
        };
        
        const mangas = await mangaService.getMangasByAuthor(decodeURIComponent(author), options);
        
        res.status(200).json({
            message: 'Mangas de l\'auteur récupérés',
            mangas,
            count: mangas.length,
            author: decodeURIComponent(author)
        });
    } catch (err) {
        console.error('Erreur récupération mangas par auteur:', err);
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};