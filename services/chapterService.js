const Chapter = require('../models/Chapter');
const Manga = require('../models/Manga');

// ==========================
//   CRÉATION D'UN CHAPITRE
// ==========================
exports.createChapter = async (chapterData) => {
    const { titre, manga, pages } = chapterData;
    
    if (!titre || !titre.trim()) {
        throw new Error('Le titre du chapitre est requis');
    }
    
    if (!manga) {
        throw new Error('L\'ID du manga est requis');
    }

    // Vérifier que le manga existe
    const existingManga = await Manga.findById(manga);
    if (!existingManga) {
        throw new Error('Manga non trouvé');
    }

    // Vérifier si un chapitre avec ce titre existe déjà pour ce manga
    const existingChapter = await Chapter.findOne({ 
        titre: { $regex: new RegExp(`^${titre.trim()}$`, 'i') },
        manga: manga
    });
    
    if (existingChapter) {
        throw new Error('Un chapitre avec ce titre existe déjà pour ce manga');
    }

    // Valider les pages si fournies
    if (pages && pages.length > 0) {
        // Vérifier que les numéros de pages sont uniques
        const pageNumbers = pages.map(page => page.numero);
        const uniqueNumbers = [...new Set(pageNumbers)];
        
        if (pageNumbers.length !== uniqueNumbers.length) {
            throw new Error('Les numéros de pages doivent être uniques');
        }

        // Vérifier que chaque page a une URL d'image
        for (let page of pages) {
            if (!page.urlImage || !page.urlImage.trim()) {
                throw new Error(`URL d'image requise pour la page ${page.numero}`);
            }
        }

        // Trier les pages par numéro
        pages.sort((a, b) => a.numero - b.numero);
    }

    const chapter = new Chapter({
        titre: titre.trim(),
        manga: manga,
        pages: pages || []
    });

    await chapter.save();

    // Ajouter ce chapitre au manga
    await Manga.findByIdAndUpdate(
        manga,
        { $addToSet: { chapitres: chapter._id } }
    );

    return await Chapter.findById(chapter._id).populate('manga', 'nom auteur');
};

// ==========================
//   RÉCUPÉRATION DE TOUS LES CHAPITRES
// ==========================
exports.getAllChapters = async (options = {}) => {
    const { 
        includeManga = false,
        includePages = false,
        sortBy = 'titre', 
        sortOrder = 1,
        limit = null,
        skip = 0,
        mangaId = null
    } = options;
    
    let query = Chapter.find({});
    
    // Filtrer par manga si spécifié
    if (mangaId) {
        query = query.find({ manga: mangaId });
    }
    
    // Population conditionnelle
    if (includeManga) {
        query = query.populate('manga', 'nom auteur urlImage');
    }
    
    // Inclure ou exclure les pages
    if (!includePages) {
        query = query.select('-pages');
    }
    
    // Tri
    const sortOptions = {};
    sortOptions[sortBy] = sortOrder;
    query = query.sort(sortOptions);
    
    // Pagination
    if (skip > 0) query = query.skip(skip);
    if (limit) query = query.limit(limit);
    
    const chapters = await query.exec();
    return chapters;
};

// ==========================
//   RÉCUPÉRATION D'UN CHAPITRE PAR ID
// ==========================
exports.getChapterById = async (id, options = {}) => {
    const { includeManga = false, includePages = true } = options;
    
    let query = Chapter.findById(id);
    
    if (includeManga) {
        query = query.populate('manga', 'nom auteur urlImage description');
    }
    
    if (!includePages) {
        query = query.select('-pages');
    }
    
    const chapter = await query.exec();
    
    if (!chapter) {
        throw new Error('Chapitre non trouvé');
    }
    
    return chapter;
};

// ==========================
//   RÉCUPÉRATION DES CHAPITRES D'UN MANGA
// ==========================
exports.getChaptersByManga = async (mangaId, options = {}) => {
    const { includePages = false, sortBy = 'titre', sortOrder = 1 } = options;
    
    // Vérifier que le manga existe
    const manga = await Manga.findById(mangaId);
    if (!manga) {
        throw new Error('Manga non trouvé');
    }
    
    let query = Chapter.find({ manga: mangaId });
    
    if (!includePages) {
        query = query.select('-pages');
    }
    
    const sortOptions = {};
    sortOptions[sortBy] = sortOrder;
    query = query.sort(sortOptions);
    
    const chapters = await query.exec();
    return chapters;
};

// ==========================
//   MISE À JOUR D'UN CHAPITRE
// ==========================
exports.updateChapter = async (id, updateData) => {
    const { titre, pages } = updateData;
    
    const chapter = await Chapter.findById(id);
    if (!chapter) {
        throw new Error('Chapitre non trouvé');
    }
    
    // Validation du titre
    if (titre !== undefined) {
        if (!titre || !titre.trim()) {
            throw new Error('Le titre du chapitre ne peut pas être vide');
        }
        
        // Vérifier si un autre chapitre avec ce titre existe pour le même manga
        const existingChapter = await Chapter.findOne({ 
            titre: { $regex: new RegExp(`^${titre.trim()}$`, 'i') },
            manga: chapter.manga,
            _id: { $ne: id }
        });
        
        if (existingChapter) {
            throw new Error('Un chapitre avec ce titre existe déjà pour ce manga');
        }
    }
    
    // Validation des pages
    if (pages !== undefined) {
        if (pages && pages.length > 0) {
            // Vérifier l'unicité des numéros
            const pageNumbers = pages.map(page => page.numero);
            const uniqueNumbers = [...new Set(pageNumbers)];
            
            if (pageNumbers.length !== uniqueNumbers.length) {
                throw new Error('Les numéros de pages doivent être uniques');
            }

            // Valider chaque page
            for (let page of pages) {
                if (!page.urlImage || !page.urlImage.trim()) {
                    throw new Error(`URL d'image requise pour la page ${page.numero}`);
                }
            }

            // Trier les pages
            pages.sort((a, b) => a.numero - b.numero);
        }
    }

    const updateFields = {};
    if (titre !== undefined) updateFields.titre = titre.trim();
    if (pages !== undefined) updateFields.pages = pages || [];

    const updatedChapter = await Chapter.findByIdAndUpdate(
        id,
        updateFields,
        { new: true, runValidators: true }
    ).populate('manga', 'nom auteur');
    
    return updatedChapter;
};

// ==========================
//   SUPPRESSION D'UN CHAPITRE
// ==========================
exports.deleteChapter = async (id) => {
    const chapter = await Chapter.findById(id);
    
    if (!chapter) {
        throw new Error('Chapitre non trouvé');
    }
    
    // Retirer ce chapitre du manga
    await Manga.findByIdAndUpdate(
        chapter.manga,
        { $pull: { chapitres: chapter._id } }
    );
    
    await Chapter.findByIdAndDelete(id);
    return chapter;
};

// ==========================
//   AJOUTER UNE PAGE À UN CHAPITRE
// ==========================
exports.addPageToChapter = async (chapterId, pageData) => {
    const { numero, urlImage } = pageData;
    
    if (!numero || !urlImage || !urlImage.trim()) {
        throw new Error('Numéro et URL d\'image de la page requis');
    }
    
    const chapter = await Chapter.findById(chapterId);
    if (!chapter) {
        throw new Error('Chapitre non trouvé');
    }
    
    // Vérifier si le numéro de page existe déjà
    const existingPage = chapter.pages.find(page => page.numero === numero);
    if (existingPage) {
        throw new Error('Une page avec ce numéro existe déjà');
    }
    
    const newPage = {
        numero: numero,
        urlImage: urlImage.trim()
    };
    
    chapter.pages.push(newPage);
    
    // Trier les pages par numéro
    chapter.pages.sort((a, b) => a.numero - b.numero);
    
    await chapter.save();
    
    return await Chapter.findById(chapterId).populate('manga', 'nom auteur');
};

// ==========================
//   SUPPRIMER UNE PAGE D'UN CHAPITRE
// ==========================
exports.removePageFromChapter = async (chapterId, pageNumber) => {
    const chapter = await Chapter.findById(chapterId);
    if (!chapter) {
        throw new Error('Chapitre non trouvé');
    }
    
    const pageIndex = chapter.pages.findIndex(page => page.numero === parseInt(pageNumber));
    if (pageIndex === -1) {
        throw new Error('Page non trouvée');
    }
    
    chapter.pages.splice(pageIndex, 1);
    await chapter.save();
    
    return await Chapter.findById(chapterId).populate('manga', 'nom auteur');
};

