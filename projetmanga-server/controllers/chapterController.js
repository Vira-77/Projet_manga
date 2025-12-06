const chapterService = require('../services/chapterService');
const { notifyNewChapter, notifyChapterUpdated } = require('../websocket/notifications');
const Chapter = require('../models/Chapter');

// ==========================
//   CRÉATION D'UN CHAPITRE
// ==========================
exports.createChapter = async (req, res) => {
    try {
        const newChapter = await chapterService.createChapter(req.body);

        // Notification temps réel pour les clients abonnés à ce manga
        if (newChapter && newChapter.manga) {
            const mangaId = newChapter.manga._id || newChapter.manga;
            notifyNewChapter(mangaId, newChapter);
        }
        
        res.status(201).json({
            message: 'Chapitre créé avec succès',
            chapter: newChapter
        });
    } catch (err) {
        console.error('Erreur création chapitre:', err);
        
        if (err.message.includes('requis') || 
            err.message.includes('non trouvé') ||
            err.message.includes('existe déjà') ||
            err.message.includes('uniques') ||
            err.message.includes('requise')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION DE TOUS LES CHAPITRES
// ==========================
exports.getAllChapters = async (req, res) => {
    try {
        const { 
            includeManga, 
            includePages, 
            sortBy, 
            sortOrder, 
            limit, 
            page,
            mangaId
        } = req.query;
        
        const options = {
            includeManga: includeManga === 'true',
            includePages: includePages === 'true',
            sortBy: sortBy || 'titre',
            sortOrder: sortOrder === 'desc' ? -1 : 1,
            limit: limit ? parseInt(limit) : null,
            skip: page ? (parseInt(page) - 1) * (parseInt(limit) || 20) : 0,
            mangaId: mangaId
        };
        
        const chapters = await chapterService.getAllChapters(options);
        
        res.status(200).json({
            message: 'Chapitres récupérés avec succès',
            chapters,
            count: chapters.length,
            page: page ? parseInt(page) : 1
        });
    } catch (err) {
        console.error('Erreur récupération chapitres:', err);
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION D'UN CHAPITRE PAR ID
// ==========================

exports.getChapterById = async (req, res) => {
    try {
        const { id } = req.params;
        const { includePages = 'true', includeManga = 'false' } = req.query;
        
        let query = Chapter.findById(id);
        
        if (includeManga === 'true') {
            query = query.populate('manga', 'nom auteur urlImage');
        }
        
        const chapter = await query;
        
        if (!chapter) {
            return res.status(404).json({ message: 'Chapitre non trouvé' });
        }
        
        //  Trouver le chapitre précédent et suivant
        const previousChapter = await Chapter.findOne({
            manga: chapter.manga,
            chapterNumber: { $lt: chapter.chapterNumber }
        }).sort({ chapterNumber: -1 }).select('_id chapterNumber titre');
        
        const nextChapter = await Chapter.findOne({
            manga: chapter.manga,
            chapterNumber: { $gt: chapter.chapterNumber }
        }).sort({ chapterNumber: 1 }).select('_id chapterNumber titre');
        
        // Ne pas inclure les pages si non demandé
        if (includePages === 'false') {
            chapter.pages = undefined;
        }
        
        res.status(200).json({
            message: 'Chapitre trouvé',
            chapter: chapter,
            navigation: {
                previous: previousChapter,
                next: nextChapter
            }
        });
    } catch (err) {
        console.error('Erreur récupération chapitre:', err);
        
        if (err.name === 'CastError') {
            return res.status(400).json({ message: 'ID de chapitre invalide' });
        }
        
        res.status(500).json({ message: 'Erreur interne du serveur' });
    }
};

// ==========================
//   RÉCUPÉRATION DES CHAPITRES D'UN MANGA
// ==========================
exports.getChaptersByManga = async (req, res) => {
    try {
        const { mangaId } = req.params;
        const { includePages, sortBy, sortOrder } = req.query;
        
        const options = {
            includePages: includePages === 'true',
            sortBy: sortBy || 'titre',
            sortOrder: sortOrder === 'desc' ? -1 : 1
        };
        
        const chapters = await chapterService.getChaptersByManga(mangaId, options);
        
        res.status(200).json({
            message: 'Chapitres du manga récupérés',
            chapters,
            count: chapters.length,
            mangaId
        });
    } catch (err) {
        console.error('Erreur récupération chapitres par manga:', err);
        
        if (err.message === 'Manga non trouvé') {
            return res.status(404).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   MISE À JOUR D'UN CHAPITRE
// ==========================
exports.updateChapter = async (req, res) => {
    try {
        const { id } = req.params;
        const updatedChapter = await chapterService.updateChapter(id, req.body);

        // Notification temps réel pour les clients abonnés à ce manga
        if (updatedChapter && updatedChapter.manga) {
            const mangaId = updatedChapter.manga._id || updatedChapter.manga;
            notifyChapterUpdated(mangaId, updatedChapter);
        }
        
        res.status(200).json({
            message: 'Chapitre mis à jour avec succès',
            chapter: updatedChapter
        });
    } catch (err) {
        console.error('Erreur mise à jour chapitre:', err);
        
        if (err.message === 'Chapitre non trouvé') {
            return res.status(404).json({ message: err.message });
        }
        
        if (err.message.includes('vide') || 
            err.message.includes('existe déjà') ||
            err.message.includes('uniques') ||
            err.message.includes('requise')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   SUPPRESSION D'UN CHAPITRE
// ==========================
exports.deleteChapter = async (req, res) => {
    try {
        const { id } = req.params;
        const deletedChapter = await chapterService.deleteChapter(id);
        
        res.status(200).json({
            message: 'Chapitre supprimé avec succès',
            chapter: deletedChapter
        });
    } catch (err) {
        console.error('Erreur suppression chapitre:', err);
        
        if (err.message === 'Chapitre non trouvé') {
            return res.status(404).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   AJOUTER UNE PAGE
// ==========================
exports.addPageToChapter = async (req, res) => {
    try {
        const { chapterId } = req.params;
        const updatedChapter = await chapterService.addPageToChapter(chapterId, req.body);
        
        res.status(200).json({
            message: 'Page ajoutée au chapitre avec succès',
            chapter: updatedChapter
        });
    } catch (err) {
        console.error('Erreur ajout page:', err);
        
        if (err.message.includes('non trouvé')) {
            return res.status(404).json({ message: err.message });
        }
        
        if (err.message.includes('requis') || err.message.includes('existe déjà')) {
            return res.status(400).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   SUPPRIMER UNE PAGE
// ==========================
exports.removePageFromChapter = async (req, res) => {
    try {
        const { chapterId, pageNumber } = req.params;
        const updatedChapter = await chapterService.removePageFromChapter(chapterId, pageNumber);
        
        res.status(200).json({
            message: 'Page supprimée du chapitre avec succès',
            chapter: updatedChapter
        });
    } catch (err) {
        console.error('Erreur suppression page:', err);
        
        if (err.message.includes('non trouvé')) {
            return res.status(404).json({ message: err.message });
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};


