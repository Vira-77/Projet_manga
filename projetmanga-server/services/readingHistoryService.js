const ReadingHistory = require('../models/ReadingHistory');

// Mettre à jour ou créer l'historique de lecture
exports.updateReadingHistory = async (userId, mangaId, source, chapterId, chapterNumber, mangaTitle, mangaImageUrl) => {
    const history = await ReadingHistory.findOneAndUpdate(
        { user: userId, mangaId },
        {
            currentChapterId: chapterId,
            currentChapterNumber: chapterNumber,
            lastReadAt: new Date(),
            source,
            title: mangaTitle,
            imageUrl: mangaImageUrl
        },
        { upsert: true, new: true }
    );
    return history;
};

// Récupérer l'historique de lecture d'un utilisateur
exports.getReadingHistory = async (userId) => {
    return await ReadingHistory.find({ user: userId })
        .sort({ lastReadAt: -1 });
};

// Récupérer l'historique d'un manga spécifique
exports.getMangaReadingHistory = async (userId, mangaId) => {
    return await ReadingHistory.findOne({ user: userId, mangaId });
};

// Supprimer un élément de l'historique
exports.deleteReadingHistory = async (userId, mangaId) => {
    return await ReadingHistory.findOneAndDelete({ user: userId, mangaId });
};

