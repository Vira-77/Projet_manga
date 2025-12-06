const Favorite = require('../models/Favorite');
const Manga = require('../models/Manga');
const User = require('../models/User');

// Obtenir les rooms Socket.io à rejoindre selon le rôle de l'utilisateur
exports.getSocketRooms = async (userId, userRole) => {
    const rooms = [];

    if (userRole === 'admin') {
        // Admin : toutes les rooms de mangas
        const allMangas = await Manga.find({}, '_id');
        allMangas.forEach(manga => {
            rooms.push(`manga:${manga._id.toString()}`);
        });
    } else if (userRole === 'admin_manga') {
        // Admin manga : uniquement ses mangas donc quand il est l'auteur
        const user = await User.findById(userId);
        if (user) {
            const myMangas = await Manga.find({ auteur: user.name });
            myMangas.forEach(manga => {
                rooms.push(`manga:${manga._id.toString()}`);
            });
        }
    } else {
        // Utilisateur normal : uniquement ses favoris
        const favorites = await Favorite.find({ user: userId });
        favorites.forEach(fav => {
            if (fav.source === 'local') {
                rooms.push(`manga:${fav.mangaId}`);
            }
        });
    }

    return rooms;
};

