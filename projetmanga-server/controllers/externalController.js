const externalService = require('../services/externalService');

exports.searchManga = async (req, res) => {
    try {
        const { q } = req.query;
        if (!q || q.trim() === '') {
            return res.status(400).json({ message: "Paramètre 'q' requis" });
        }

        const data = await externalService.searchManga(q.trim());
        res.status(200).json({ results: data.data });

    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.getMangaById = async (req, res) => {
    try {
        const { id } = req.params;
        const data = await externalService.getMangaById(id);

        res.status(200).json({ manga: data.data });

    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};

exports.getTopMangas = async (req, res) => {
    try {
        const data = await externalService.getTopMangas();
        res.status(200).json({ top: data.data });
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
};


exports.getAllGenres = async (req, res) => {
    try {
        const genresResponse = await externalService.getAllGenres();
        res.status(200).json({
            message: "Genres Jikan récupérés",
            genres: genresResponse
        });
    } catch (err) {
        console.error("Erreur getAllGenres:", err.message);
        res.status(500).json({ message: "Erreur serveur Jikan" });
    }
};


exports.getMangaByGenre = async (req, res) => {
    try {
        const { id } = req.params;
        const { page = 1, limit = 20 } = req.query;

        const { results, pagination } = await externalService.getMangaByGenre(id, page, limit);

        res.status(200).json({
            message: "Mangas récupérés pour ce genre",
            genreId: id,
            results,
            pagination
        });
    } catch (err) {
        console.error("Erreur getMangaByGenre:", err.message);
        res.status(500).json({ message: "Erreur serveur Jikan" });
    }
};



exports.searchGenreAndMangas = async (req, res) => {
    try {
        const { name } = req.params;
        const { page = 1, limit = 20 } = req.query;

        const { genre, results, pagination } =
            await externalService.searchGenreAndMangas(name, page, limit);

        res.status(200).json({
            message: `Genre '${name}' trouvé`,
            genre,
            results,
            pagination
        });
    } catch (err) {
        console.error("Erreur searchGenreAndMangas:", err.message);
        res.status(404).json({ message: "Genre introuvable ou erreur Jikan" });
    }
};

