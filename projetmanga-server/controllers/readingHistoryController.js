const readingHistoryService = require('../services/readingHistoryService');

// Mettre à jour l'historique de lecture
exports.updateReadingHistory = async (req, res) => {
    try {
        const { mangaId, source, chapterId, chapterNumber, title, imageUrl } = req.body;
        const userId = req.user.id;

        if (!mangaId || !source) {
            return res.status(400).json({ message: 'mangaId et source sont requis' });
        }

        const history = await readingHistoryService.updateReadingHistory(
            userId,
            mangaId,
            source,
            chapterId,
            chapterNumber,
            title,
            imageUrl
        );

        res.status(200).json({
            message: 'Historique mis à jour',
            history
        });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

// Récupérer l'historique de lecture
exports.getReadingHistory = async (req, res) => {
    try {
        const userId = req.user.id;
        const history = await readingHistoryService.getReadingHistory(userId);
        res.status(200).json({ history });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

// Récupérer l'historique d'un manga spécifique
exports.getMangaReadingHistory = async (req, res) => {
    try {
        const userId = req.user.id;
        const { mangaId } = req.params;
        const history = await readingHistoryService.getMangaReadingHistory(userId, mangaId);
        
        if (!history) {
            return res.status(404).json({ message: 'Aucun historique trouvé pour ce manga' });
        }
        
        res.status(200).json({ history });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

// Supprimer un élément de l'historique
exports.deleteReadingHistory = async (req, res) => {
    try {
        const userId = req.user.id;
        const { mangaId } = req.params;
        const deleted = await readingHistoryService.deleteReadingHistory(userId, mangaId);
        
        if (!deleted) {
            return res.status(404).json({ message: 'Historique non trouvé' });
        }
        
        res.status(200).json({ message: 'Historique supprimé', history: deleted });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

