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
    const favorites = await Favorite.find({ user: userId }).sort({ createdAt: -1 });
    
    // Transformer les favoris en mangas complets
    const mangas = await Promise.all(favorites.map(async (fav) => {
        if (fav.source === 'local') {
            // Récupérer le manga local complet
            const manga = await Manga.findById(fav.mangaId).populate('genres');
            if (manga) {
                // Convertir les genres en liste d'IDs (strings) pour correspondre au modèle Android
                const genreIds = manga.genres ? manga.genres.map(g => g._id ? g._id.toString() : g.toString()) : [];
                return {
                    _id: manga._id,
                    nom: manga.nom,
                    description: manga.description,
                    dateDeSortie: manga.dateDeSortie,
                    urlImage: manga.urlImage,
                    auteur: manga.auteur,
                    genres: genreIds,
                    jikanId: manga.jikanId,
                    source: 'local'
                };
            }
        } else if (fav.source === 'jikan') {
            // Pour Jikan, retourner les données du favori formatées comme un manga
            try {
                const jikanData = await externalService.getMangaById(fav.mangaId);
                // Convertir les genres Jikan en liste d'IDs (strings) pour correspondre au modèle Android
                const genreIds = jikanData.data.genres?.map(g => String(g.mal_id)) || [];
                return {
                    _id: fav.mangaId,
                    nom: jikanData.data.title,
                    description: jikanData.data.synopsis,
                    dateDeSortie: jikanData.data.published?.from ? new Date(jikanData.data.published.from) : null,
                    urlImage: jikanData.data.images?.jpg?.image_url || fav.imageUrl,
                    auteur: jikanData.data.authors?.[0]?.name || 'Inconnu',
                    genres: genreIds,
                    jikanId: parseInt(fav.mangaId),
                    source: 'jikan'
                };
            } catch (err) {
                // Si erreur Jikan, retourner les données du favori
                return {
                    _id: fav.mangaId,
                    nom: fav.title || 'Manga inconnu',
                    description: null,
                    dateDeSortie: null,
                    urlImage: fav.imageUrl,
                    auteur: 'Inconnu',
                    genres: [],
                    jikanId: parseInt(fav.mangaId),
                    source: 'jikan'
                };
            }
        }
        return null;
    }));
    
    // Filtrer les nulls
    return mangas.filter(m => m !== null);
};
