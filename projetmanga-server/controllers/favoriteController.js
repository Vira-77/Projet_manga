const favoriteService = require('../services/favoriteService');

exports.addFavorite = async (req, res) => {
    try {
        const { mangaId, source } = req.body;

        const fav = await favoriteService.addFavorite(
            req.user.id,
            mangaId,
            source
        );

        res.status(201).json({ message: "Ajouté aux favoris", favorite: fav });

    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

exports.removeFavorite = async (req, res) => {
    try {
        const { mangaId } = req.params;

        const fav = await favoriteService.removeFavorite(
            req.user.id,
            mangaId
        );

        res.status(200).json({ message: "Supprimé des favoris", favorite: fav });

    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};

exports.getFavorites = async (req, res) => {
    try {
        const favs = await favoriteService.getFavorites(req.user.id);
        res.status(200).json({ favorites: favs });

    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.getFavoritesByUserId = async (req, res) => {
    try {
        const { userId } = req.params;
        // Vérifier que l'utilisateur peut accéder à ces favoris (soit lui-même, soit admin)
        if (req.user.id !== userId && req.user.role !== 'admin') {
            return res.status(403).json({ message: 'Accès non autorisé' });
        }
        const favs = await favoriteService.getFavorites(userId);
        res.status(200).json({ favorites: favs });

    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};