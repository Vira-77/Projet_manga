const genreService = require('../services/genreService');

// ==========================
//   CRÉATION D'UN GENRE
// ==========================
exports.createGenre = async (req, res) => {
    try {
        const newGenre = await genreService.createGenre(req.body);
        
        res.status(201).json({
            message: 'Genre créé avec succès',
            genre: newGenre
        });
    } catch (err) {
        console.error('Erreur création genre:', err);
        
        if (err.message.includes('existe déjà') || err.message.includes('requis')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION DE TOUS LES GENRES
// ==========================
exports.getAllGenres = async (req, res) => {
    try {
        const { includeMangas, sortBy, sortOrder } = req.query;
        
        const options = {
            includeMangas: includeMangas === 'true',
            sortBy: sortBy || 'name',
            sortOrder: sortOrder === 'desc' ? -1 : 1
        };
        
        const genres = await genreService.getAllGenres(options);
        
        res.status(200).json({
            message: 'Genres récupérés avec succès',
            genres,
            count: genres.length
        });
    } catch (err) {
        console.error('Erreur récupération genres:', err);
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION D'UN GENRE PAR ID
// ==========================
exports.getGenreById = async (req, res) => {
    try {
        const { id } = req.params;
        const { includeMangas } = req.query;
        
        const genre = await genreService.getGenreById(id, includeMangas === 'true');
        
        res.status(200).json({
            message: 'Genre trouvé',
            genre
        });
    } catch (err) {
        console.error('Erreur récupération genre:', err);
        const statusCode = err.message === 'Genre non trouvé' ? 404 : 500;
        res.status(statusCode).json({ 
            message: err.message || 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION D'UN GENRE PAR NOM
// ==========================
exports.getGenreByName = async (req, res) => {
    try {
        const { name } = req.params;
        const { includeMangas } = req.query;
        
        const genre = await genreService.getGenreByName(name, includeMangas === 'true');
        
        res.status(200).json({
            message: 'Genre trouvé',
            genre
        });
    } catch (err) {
        console.error('Erreur récupération genre par nom:', err);
        const statusCode = err.message === 'Genre non trouvé' ? 404 : 500;
        res.status(statusCode).json({ 
            message: err.message || 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   MISE À JOUR D'UN GENRE
// ==========================
exports.updateGenre = async (req, res) => {
    try {
        const { id } = req.params;
        const updatedGenre = await genreService.updateGenre(id, req.body);
        
        res.status(200).json({
            message: 'Genre mis à jour avec succès',
            genre: updatedGenre
        });
    } catch (err) {
        console.error('Erreur mise à jour genre:', err);
        
        if (err.message === 'Genre non trouvé') {
            return res.status(404).json({ message: err.message });
        }
        
        if (err.message.includes('existe déjà') || err.message.includes('vide')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   SUPPRESSION D'UN GENRE
// ==========================
exports.deleteGenre = async (req, res) => {
    try {
        const { id } = req.params;
        const deletedGenre = await genreService.deleteGenre(id);
        
        res.status(200).json({
            message: 'Genre supprimé avec succès',
            genre: deletedGenre
        });
    } catch (err) {
        console.error('Erreur suppression genre:', err);
        
        if (err.message === 'Genre non trouvé') {
            return res.status(404).json({ message: err.message });
        }
        
        if (err.message.includes('contient des mangas')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   AJOUTER UN MANGA À UN GENRE
// ==========================
exports.addMangaToGenre = async (req, res) => {
    try {
        const { genreId, mangaId } = req.params;
        const updatedGenre = await genreService.addMangaToGenre(genreId, mangaId);
        
        res.status(200).json({
            message: 'Manga ajouté au genre avec succès',
            genre: updatedGenre
        });
    } catch (err) {
        console.error('Erreur ajout manga au genre:', err);
        
        if (err.message.includes('non trouvé')) {
            return res.status(404).json({ message: err.message });
        }
        
        if (err.message.includes('déjà dans')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RETIRER UN MANGA D'UN GENRE
// ==========================
exports.removeMangaFromGenre = async (req, res) => {
    try {
        const { genreId, mangaId } = req.params;
        const updatedGenre = await genreService.removeMangaFromGenre(genreId, mangaId);
        
        res.status(200).json({
            message: 'Manga retiré du genre avec succès',
            genre: updatedGenre
        });
    } catch (err) {
        console.error('Erreur retrait manga du genre:', err);
        
        if (err.message.includes('non trouvé')) {
            return res.status(404).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

