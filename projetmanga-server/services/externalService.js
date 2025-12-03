const axios = require('axios');

const JIKAN_BASE_URL = 'https://api.jikan.moe/v4';

// Fonction utilitaire pour faire les requêtes Jikan
async function jikanRequest(endpoint, params = {}) {
    try {
        const response = await axios.get(`${JIKAN_BASE_URL}${endpoint}`, {
            params,
            timeout: 7000
        });

        return response.data;
    } catch (err) {
        console.error("🔥 ERREUR API Jikan :", err.response?.data || err.message);

        if (err.response) {
            throw new Error(`Erreur API Jikan (${err.response.status})`);
        }
        if (err.code === 'ECONNABORTED') {
            throw new Error('Timeout API Jikan');
        }
        throw new Error('Erreur de connexion à l’API Jikan');
    }
}

/* ============================
   🔎 RECHERCHE MANGA (nom)
   ============================ */
exports.searchManga = async (query) => {
    return await jikanRequest('/manga', { q: query, limit: 15 });
};

/* ============================
   📘 MANGA PAR ID
   ============================ */
exports.getMangaById = async (id) => {
    return await jikanRequest(`/manga/${id}`);
};

/* ============================
   🏆 TOP MANGAS
   ============================ */
exports.getTopMangas = async () => {
    return await jikanRequest('/top/manga', { limit: 20 });
};

/* ============================
   🎭 TOUS LES GENRES
   ============================ */
exports.getAllGenres = async () => {
    const response = await jikanRequest('/genres/manga');
    return response.data;  // liste brute des genres
};

/* ============================
   📚 MANGAS PAR GENRE
   ============================ */
exports.getMangaByGenre = async (genreId, page = 1, limit = 20) => {
    const response = await jikanRequest('/manga', {
        genres: genreId,
        page,
        limit
    });

    return {
        results: response.data,
        pagination: response.pagination
    };
};

/* ============================================
   🔍 RECHERCHE PAR GENRE (nom → genreId → mangas)
   ============================================ */
exports.searchGenreAndMangas = async (name, page = 1, limit = 20) => {
    // 1️⃣ Récupérer tous les genres
    const genresResponse = await jikanRequest('/genres/manga');
    const genres = genresResponse.data;

    // 2️⃣ Trouver un genre par nom (case insensible)
    const genre = genres.find(g => 
        g.name.toLowerCase() === name.toLowerCase()
    );

    if (!genre) throw new Error("Genre introuvable");

    // 3️⃣ Récupérer les mangas du genre trouvé
    const mangas = await exports.getMangaByGenre(genre.mal_id, page, limit);

    return {
        genre,
        ...mangas
    };
};
