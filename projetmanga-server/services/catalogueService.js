const Manga = require("../models/Manga");

let cachedCatalogue = null;

async function loadCatalogue() {
    if (!cachedCatalogue) {
        console.log("Catalogue chargé !");
        const mangas = await Manga.find().lean();

        cachedCatalogue = mangas.map(m => ({
            nom: m.nom
        }));
    }
    return cachedCatalogue;
}

module.exports = { loadCatalogue };
