const Genre = require('../models/Genre');

// ==========================
//   CRÉATION D'UN GENRE
// ==========================
exports.createGenre = async (genreData) => {
    const { name, description } = genreData;
    
    if (!name || !name.trim()) {
        throw new Error('Le nom du genre est requis');
    }

    // Vérifier si le genre existe déjà
    const existingGenre = await Genre.findOne({ 
        name: { $regex: new RegExp(`^${name.trim()}$`, 'i') } // Insensible à la casse
    });
    
    if (existingGenre) {
        throw new Error('Un genre avec ce nom existe déjà');
    }

    const genre = new Genre({
        name: name.trim(),
        description: description?.trim() || '',
        mangas: []
    });

    await genre.save();
    return genre;
};

// ==========================
//   RÉCUPÉRATION DE TOUS LES GENRES
// ==========================
exports.getAllGenres = async (options = {}) => {
    const { includeMangas = false, sortBy = 'name', sortOrder = 1 } = options;
    
    let query = Genre.find({});
    
    if (includeMangas) {
        query = query.populate('mangas', 'nom auteur urlImage');
    }
    
    // Tri dynamique
    const sortOptions = {};
    sortOptions[sortBy] = sortOrder;
    query = query.sort(sortOptions);
    
    const genres = await query.exec();
    return genres;
};

// ==========================
//   RÉCUPÉRATION D'UN GENRE PAR ID
// ==========================
exports.getGenreById = async (id, includeMangas = false) => {
    let query = Genre.findById(id);
    
    if (includeMangas) {
        query = query.populate('mangas', 'nom auteur urlImage');
    }
    
    const genre = await query.exec();
    
    if (!genre) {
        throw new Error('Genre non trouvé');
    }
    
    return genre;
};

// ==========================
//   RÉCUPÉRATION D'UN GENRE PAR NOM
// ==========================
exports.getGenreByName = async (name, includeMangas = false) => {
    let query = Genre.findOne({ 
        name: { $regex: new RegExp(`^${name}$`, 'i') } 
    });
    
    if (includeMangas) {
        query = query.populate('mangas', 'nom auteur urlImage');
    }
    
    const genre = await query.exec();
    
    if (!genre) {
        throw new Error('Genre non trouvé');
    }
    
    return genre;
};

// ==========================
//   MISE À JOUR D'UN GENRE
// ==========================
exports.updateGenre = async (id, updateData) => {
    const { name, description } = updateData;
    
    // Validation si le nom est fourni
    if (name !== undefined) {
        if (!name || !name.trim()) {
            throw new Error('Le nom du genre ne peut pas être vide');
        }
        
        // Vérifier si un autre genre avec ce nom existe
        const existingGenre = await Genre.findOne({ 
            name: { $regex: new RegExp(`^${name.trim()}$`, 'i') },
            _id: { $ne: id } // Exclure le genre actuel
        });
        
        if (existingGenre) {
            throw new Error('Un genre avec ce nom existe déjà');
        }
    }

    const updateFields = {};
    if (name !== undefined) updateFields.name = name.trim();
    if (description !== undefined) updateFields.description = description?.trim() || '';

    const genre = await Genre.findByIdAndUpdate(
        id, 
        updateFields, 
        { new: true, runValidators: true }
    ).populate('mangas','nom auteur urlImage');
    
    if (!genre) {
        throw new Error('Genre non trouvé');
    }
    
    return genre;
};

// ==========================
//   SUPPRESSION D'UN GENRE
// ==========================
exports.deleteGenre = async (id) => {
    const genre = await Genre.findById(id);
    
    if (!genre) {
        throw new Error('Genre non trouvé');
    }
    
    // Vérifier s'il y a des mangas associés
    if (genre.mangas && genre.mangas.length > 0) {
        throw new Error('Impossible de supprimer un genre qui contient des mangas');
    }
    
    await Genre.findByIdAndDelete(id);
    return genre;
};

// ==========================
//   AJOUTER UN MANGA À UN GENRE
// ==========================
exports.addMangaToGenre = async (genreId, mangaId) => {
    const genre = await Genre.findById(genreId);
    
    if (!genre) {
        throw new Error('Genre non trouvé');
    }
    
    // Vérifier si le manga n'est pas déjà dans le genre
    if (genre.mangas.includes(mangaId)) {
        throw new Error('Le manga est déjà dans ce genre');
    }
    
    genre.mangas.push(mangaId);
    await genre.save();
    
    return await Genre.findById(genreId).populate('mangas', 'nom auteur urlImage');
};

// ==========================
//   RETIRER UN MANGA D'UN GENRE
// ==========================
exports.removeMangaFromGenre = async (genreId, mangaId) => {
    const genre = await Genre.findById(genreId);
    
    if (!genre) {
        throw new Error('Genre non trouvé');
    }
    
    // Retirer le manga du tableau
    genre.mangas = genre.mangas.filter(id => !id.equals(mangaId));
    await genre.save();
    
    return await Genre.findById(genreId).populate('mangas', 'nom auteur urlImage');
};

