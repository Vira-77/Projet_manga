const Manga = require("../models/Manga");

async function loadCatalogue() {
    const mangas = await Manga.find().lean();

    return mangas.map(m => ({
        title: m.nom || "",
        genres: m.genres || [], // ObjectIds acceptés
    }));
}

module.exports = { loadCatalogue };
