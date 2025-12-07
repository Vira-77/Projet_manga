const Manga = require('../models/Manga');
const Genre = require('../models/Genre');

// ==========================
//   CRÉATION D'UN MANGA
// ==========================
exports.createManga = async (mangaData) => {
    const { nom, description, dateDeSortie, urlImage, auteur, genres } = mangaData;
    
    if (!nom || !nom.trim()) {
        throw new Error('Le nom du manga est requis');
    }
    
    if (!auteur || !auteur.trim()) {
        throw new Error('Le nom de l\'auteur est requis');
    }

    // Vérifier si le manga existe déjà
    const existingManga = await Manga.findOne({ 
        nom: { $regex: new RegExp(`^${nom.trim()}$`, 'i') },
        auteur: { $regex: new RegExp(`^${auteur.trim()}$`, 'i') }
    });
    
    if (existingManga) {
        throw new Error('Un manga avec ce nom et cet auteur existe déjà');
    }

    // Valider les genres si fournis
    if (genres && genres.length > 0) {
        const validGenres = await Genre.find({ _id: { $in: genres } });
        if (validGenres.length !== genres.length) {
            throw new Error('Un ou plusieurs genres sont invalides');
        }
    }

    // Valider la date de sortie
    if (dateDeSortie && new Date(dateDeSortie) > new Date()) {
        throw new Error('La date de sortie ne peut pas être dans le futur');
    }

    const manga = new Manga({
        nom: nom.trim(),
        description: description?.trim(),
        dateDeSortie: dateDeSortie ? new Date(dateDeSortie) : undefined,
        urlImage: urlImage?.trim(),
        auteur: auteur.trim(),
        genres: genres || [],
        chapitres: []
    });

    await manga.save();

    // Ajouter le manga aux genres correspondants
    if (genres && genres.length > 0) {
        await Genre.updateMany(
            { _id: { $in: genres } },
            { $addToSet: { mangas: manga._id } }
        );
    }

    return await Manga.findById(manga._id).populate('genres', 'name description');
};

// ==========================
//   RÉCUPÉRATION DE TOUS LES MANGAS
// ==========================
exports.getAllMangas = async (options = {}) => {
    const { 
        includeGenres = false, 
        includeChapters = false,
        sortBy = 'nom', 
        sortOrder = 1,
        limit = null,
        skip = 0,
        search = null
    } = options;
    
    let query = Manga.find({});
    
    // Recherche par nom ou auteur
    if (search && search.trim()) {
        query = query.find({
            $or: [
                { nom: { $regex: search.trim(), $options: 'i' } },
                { auteur: { $regex: search.trim(), $options: 'i' } },
                { description: { $regex: search.trim(), $options: 'i' } }
            ]
        });
    }
    
    // Population conditionnelle
    if (includeGenres) {
        query = query.populate('genres', 'name description');
    }
    
    if (includeChapters) {
        query = query.populate('chapitres', 'title number publishedAt');
    }
    
    // Tri
    const sortOptions = {};
    sortOptions[sortBy] = sortOrder;
    query = query.sort(sortOptions);
    
    // Pagination
    if (skip > 0) query = query.skip(skip);
    if (limit) query = query.limit(limit);
    
    const mangas = await query.exec();
    return mangas;
};

// ==========================
//   RÉCUPÉRATION D'UN MANGA PAR ID
// ==========================
exports.getMangaById = async (id, options = {}) => {
    const { includeGenres = false, includeChapters = false } = options;

    console.log('🔍 Recherche manga avec ID:', id);
        console.log('🔍 Type de l\'ID:', typeof id);
        console.log('🔍 Longueur de l\'ID:', id?.length);
    
    let query = Manga.findById(id);
    
    if (includeGenres) {
        query = query.populate('genres', 'name description');
    }
    
    if (includeChapters) {
        query = query.populate('chapitres', 'title number publishedAt content');
    }
    
    const manga = await query.exec();
    
    if (!manga) {
        throw new Error('Manga non trouvé');
    }
    
    return manga;
};

// ==========================
//   RÉCUPÉRATION PAR NOM
// ==========================
exports.getMangaByName = async (nom, options = {}) => {
    const { includeGenres = false, includeChapters = false } = options;
    
    let query = Manga.findOne({ 
        nom: { $regex: new RegExp(`^${nom}$`, 'i') } 
    });
    
    if (includeGenres) {
        query = query.populate('genres', 'name description');
    }
    
    if (includeChapters) {
        query = query.populate('chapitres', 'title number publishedAt');
    }
    
    const manga = await query.exec();
    
    if (!manga) {
        throw new Error('Manga non trouvé');
    }
    
    return manga;
};

// ==========================
//   MISE À JOUR D'UN MANGA
// ==========================
exports.updateManga = async (id, updateData) => {
    const { nom, description, dateDeSortie, urlImage, auteur, genres } = updateData;
    
    const manga = await Manga.findById(id);
    if (!manga) {
        throw new Error('Manga non trouvé');
    }
    
    // Validation si le nom est fourni
    if (nom !== undefined) {
        if (!nom || !nom.trim()) {
            throw new Error('Le nom du manga ne peut pas être vide');
        }
        
        // Vérifier si un autre manga avec ce nom et auteur existe
        const existingManga = await Manga.findOne({ 
            nom: { $regex: new RegExp(`^${nom.trim()}$`, 'i') },
            auteur: auteur ? { $regex: new RegExp(`^${auteur.trim()}$`, 'i') } : manga.auteur,
            _id: { $ne: id }
        });
        
        if (existingManga) {
            throw new Error('Un manga avec ce nom et cet auteur existe déjà');
        }
    }
    
    // Validation de l'auteur
    if (auteur !== undefined && (!auteur || !auteur.trim())) {
        throw new Error('Le nom de l\'auteur ne peut pas être vide');
    }
    
    // Validation des genres
    if (genres && genres.length > 0) {
        const validGenres = await Genre.find({ _id: { $in: genres } });
        if (validGenres.length !== genres.length) {
            throw new Error('Un ou plusieurs genres sont invalides');
        }
    }
    
    // Validation de la date
    if (dateDeSortie && new Date(dateDeSortie) > new Date()) {
        throw new Error('La date de sortie ne peut pas être dans le futur');
    }

    // Préparer les champs à mettre à jour
    const updateFields = {};
    if (nom !== undefined) updateFields.nom = nom.trim();
    if (description !== undefined) updateFields.description = description?.trim();
    if (dateDeSortie !== undefined) updateFields.dateDeSortie = dateDeSortie ? new Date(dateDeSortie) : null;
    if (urlImage !== undefined) updateFields.urlImage = urlImage?.trim();
    if (auteur !== undefined) updateFields.auteur = auteur.trim();
    if (genres !== undefined) updateFields.genres = genres;

    // Gestion des changements de genres
    if (genres !== undefined) {
        // Retirer ce manga des anciens genres
        await Genre.updateMany(
            { mangas: manga._id },
            { $pull: { mangas: manga._id } }
        );
        
        // Ajouter ce manga aux nouveaux genres
        if (genres.length > 0) {
            await Genre.updateMany(
                { _id: { $in: genres } },
                { $addToSet: { mangas: manga._id } }
            );
        }
    }

    const updatedManga = await Manga.findByIdAndUpdate(
        id,
        updateFields,
        { new: true, runValidators: true }
    ).populate('genres', 'name description');
    
    return updatedManga;
};

// ==========================
//   SUPPRESSION D'UN MANGA
// ==========================
exports.deleteManga = async (id) => {
    const manga = await Manga.findById(id);
    
    if (!manga) {
        throw new Error('Manga non trouvé');
    }
    
    // Retirer ce manga de tous les genres
    await Genre.updateMany(
        { mangas: manga._id },
        { $pull: { mangas: manga._id } }
    );
    
    await Manga.findByIdAndDelete(id);
    return manga;
};

// ==========================
//   RECHERCHE DE MANGAS
// ==========================
exports.searchMangas = async (searchTerm, options = {}) => {
    if (!searchTerm || !searchTerm.trim()) {
        throw new Error('Terme de recherche requis');
    }
    
    const { includeGenres = false, limit = 20 } = options;
    
    let query = Manga.find({
        $or: [
            { nom: { $regex: searchTerm.trim(), $options: 'i' } },
            { auteur: { $regex: searchTerm.trim(), $options: 'i' } },
            { description: { $regex: searchTerm.trim(), $options: 'i' } }
        ]
    }).limit(limit).sort({ nom: 1 });
    
    if (includeGenres) {
        query = query.populate('genres', 'name');
    }
    
    const mangas = await query.exec();
    return mangas;
};

// ==========================
//   MANGAS PAR GENRE
// ==========================
exports.getMangasByGenre = async (genreId, options = {}) => {
    const { limit = 20, skip = 0 } = options;
    
    // Vérifier que le genre existe
    const genre = await Genre.findById(genreId);
    if (!genre) {
        throw new Error('Genre non trouvé');
    }
    
    const mangas = await Manga.find({ 
        genres: genreId 
    })
    .populate('genres', 'name')
    .sort({ nom: 1 })
    .skip(skip)
    .limit(limit);
    
    return mangas;
};

// ==========================
//   MANGAS PAR AUTEUR
// ==========================
exports.getMangasByAuthor = async (auteur, options = {}) => {
    const { includeGenres = false, limit = 20 } = options;
    
    let query = Manga.find({
        auteur: { $regex: new RegExp(`^${auteur}$`, 'i') }
    }).sort({ dateDeSortie: -1 }).limit(limit);
    
    if (includeGenres) {
        query = query.populate('genres', 'name');
    }
    
    const mangas = await query.exec();
    return mangas;
};