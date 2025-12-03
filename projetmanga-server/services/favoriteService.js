const Favorite = require('../models/Favorite');
const Manga = require('../models/Manga');
const externalService = require('./externalService');

exports.addFavorite = async (userId, mangaId, source) => {
    if (!['local', 'jikan'].includes(source)) {
        throw new Error("Source invalide : 'local' ou 'jikan'");
    }

    let title, imageUrl;

    if (source === 'local') {
        const manga = await Manga.findById(mangaId);
        if (!manga) throw new Error("Manga local introuvable");

        title = manga.nom;
        imageUrl = manga.urlImage;
    } else {
        const data = await externalService.getMangaById(mangaId);
        title = data.data.title;
        imageUrl = data.data.images.jpg.image_url;
    }

    const fav = new Favorite({
        user: userId,
        mangaId,
        source,
        title,
        imageUrl
    });

    await fav.save();
    return fav;
};

exports.removeFavorite = async (userId, mangaId) => {
    const fav = await Favorite.findOneAndDelete({ user: userId, mangaId });
    if (!fav) throw new Error("Favori introuvable");
    return fav;
};

exports.getFavorites = async (userId) => {
    return await Favorite.find({ user: userId }).sort({ createdAt: -1 });
};
