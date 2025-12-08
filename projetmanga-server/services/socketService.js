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
        // Admin manga : ses mangas (quand il est l'auteur) + ses favoris
        const user = await User.findById(userId);
        if (user) {
            // Ses propres mangas
            const myMangas = await Manga.find({ auteur: user.name });
            console.log(`[SocketService] Admin manga ${user.name} - Mangas trouvés: ${myMangas.length}`);
            myMangas.forEach(manga => {
                rooms.push(`manga:${manga._id.toString()}`);
            });
            
            // Ses favoris (mangas locaux seulement)
            const favorites = await Favorite.find({ user: userId });
            console.log(`[SocketService] Admin manga ${user.name} - Favoris trouvés: ${favorites.length}`);
            favorites.forEach(fav => {
                if (fav.source === 'local') {
                    rooms.push(`manga:${fav.mangaId}`);
                }
            });
        }
    } else {
        // Utilisateur normal : uniquement ses favoris
        const favorites = await Favorite.find({ user: userId });
        console.log(`[SocketService] User normal ${userId} - Favoris trouvés: ${favorites.length}`);
        favorites.forEach(fav => {
            console.log(`[SocketService] Favori: mangaId=${fav.mangaId}, source=${fav.source}`);
            if (fav.source === 'local') {
                rooms.push(`manga:${fav.mangaId}`);
                console.log(`[SocketService] Room ajoutée: manga:${fav.mangaId}`);
            } else {
                console.log(`[SocketService] Favori ignoré (source=${fav.source}, pas 'local')`);
            }
        });
    }

    console.log(`[SocketService] Rooms générées pour ${userRole} (${userId}): ${rooms.length} rooms`);
    return rooms;
};

