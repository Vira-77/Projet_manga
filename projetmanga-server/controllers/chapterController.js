const Chapter = require('../models/Chapter');
const Manga = require('../models/Manga');
const path = require('path');
const fs = require('fs');
const { notifyNewChapter, notifyChapterUpdated } = require('../websocket/notifications');

// =============================
// CRÉER UN CHAPITRE (sans pages)
// =============================
exports.createChapter = async (req, res) => {
    try {
        const { titre, manga, chapterNumber } = req.body;

        console.log('📖 Création chapitre:', { titre, manga, chapterNumber });

        // Vérifier que le manga existe
        const mangaExists = await Manga.findById(manga);
        if (!mangaExists) {
            return res.status(404).json({ message: 'Manga non trouvé' });
        }

        // Créer le chapitre (sans pages)
        const newChapter = await Chapter.create({
            titre: titre || `Chapitre ${chapterNumber}`,
            manga,
            chapterNumber,
            pages: []
        });

        // Ajouter le chapitre au manga
        await Manga.findByIdAndUpdate(
            manga,
            { $push: { chapitres: newChapter._id } }
        );

        console.log('✅ Chapitre créé:', newChapter._id);
        console.log('📢 Envoi notification pour manga:', manga.toString());

        // Envoyer une notification aux utilisateurs qui suivent ce manga
        try {
            notifyNewChapter(manga.toString(), newChapter.toObject());
            console.log('✅ Notification envoyée avec succès');
        } catch (notifError) {
            console.error('❌ Erreur envoi notification:', notifError);
            // Ne pas bloquer la création du chapitre si la notification échoue
        }

        res.status(201).json(newChapter);
    } catch (error) {
        console.error('❌ Erreur création chapitre:', error);
        res.status(500).json({ 
            message: 'Erreur serveur lors de la création du chapitre',
            error: error.message 
        });
    }
};

// =============================
// AJOUTER UNE PAGE À UN CHAPITRE
// =============================
exports.addPageToChapter = async (req, res) => {
    try {
        const { chapterId } = req.params;
        const { numero } = req.body;

        console.log('📸 Ajout page au chapitre:', chapterId, 'Numéro:', numero);

        // Vérifier qu'un fichier a été uploadé
        if (!req.file) {
            return res.status(400).json({ message: 'Aucune image fournie' });
        }

        // Trouver le chapitre
        const chapter = await Chapter.findById(chapterId);
        if (!chapter) {
            // Supprimer le fichier uploadé si le chapitre n'existe pas
            fs.unlinkSync(req.file.path);
            return res.status(404).json({ message: 'Chapitre non trouvé' });
        }

        // Construire l'URL de l'image (comme pour la photo de profil)
        const imageUrl = `/uploads/chapters/${req.file.filename}`;

        console.log('📷 Image uploadée:', imageUrl);

        // Ajouter la page
        chapter.pages.push({
            numero: parseInt(numero),
            urlImage: imageUrl
        });

        // Trier les pages par numéro
        chapter.pages.sort((a, b) => a.numero - b.numero);

        await chapter.save();

        console.log('✅ Page ajoutée avec succès');

        res.json({
            message: 'Page ajoutée avec succès',
            chapter: chapter
        });

    } catch (error) {
        console.error('❌ Erreur ajout page:', error);
        
        // Supprimer le fichier en cas d'erreur
        if (req.file && req.file.path) {
            try {
                fs.unlinkSync(req.file.path);
            } catch (unlinkError) {
                console.error('Erreur suppression fichier:', unlinkError);
            }
        }

        res.status(500).json({ 
            message: 'Erreur serveur lors de l\'ajout de la page',
            error: error.message 
        });
    }
};

// =============================
// SUPPRIMER UNE PAGE D'UN CHAPITRE
// =============================
exports.removePageFromChapter = async (req, res) => {
    try {
        const { chapterId, pageNumber } = req.params;

        console.log('🗑️ Suppression page:', pageNumber, 'du chapitre:', chapterId);

        const chapter = await Chapter.findById(chapterId);
        if (!chapter) {
            return res.status(404).json({ message: 'Chapitre non trouvé' });
        }

        // Trouver la page à supprimer
        const pageToDelete = chapter.pages.find(p => p.numero === parseInt(pageNumber));
        
        if (!pageToDelete) {
            return res.status(404).json({ message: 'Page non trouvée' });
        }

        // Supprimer le fichier image
        if (pageToDelete.urlImage) {
            const imagePath = path.join(__dirname, '../public', pageToDelete.urlImage);
            if (fs.existsSync(imagePath)) {
                fs.unlinkSync(imagePath);
                console.log('🗑️ Fichier image supprimé:', imagePath);
            }
        }

        // Retirer la page du tableau
        chapter.pages = chapter.pages.filter(p => p.numero !== parseInt(pageNumber));

        await chapter.save();

        console.log('✅ Page supprimée avec succès');

        res.json({
            message: 'Page supprimée avec succès',
            chapter: chapter
        });

    } catch (error) {
        console.error('❌ Erreur suppression page:', error);
        res.status(500).json({ 
            message: 'Erreur serveur lors de la suppression de la page',
            error: error.message 
        });
    }
};

// =============================
// RÉCUPÉRER LES CHAPITRES D'UN MANGA
// =============================
exports.getChaptersByManga = async (req, res) => {
    try {
        const { mangaId } = req.params;

        const chapters = await Chapter.find({ manga: mangaId })
            .sort({ chapterNumber: 1 });

        res.json(chapters);
    } catch (error) {
        console.error('❌ Erreur récupération chapitres:', error);
        res.status(500).json({ 
            message: 'Erreur serveur',
            error: error.message 
        });
    }
};


// =============================
// RÉCUPÉRER LES CHAPITRES D'UN MANGA
// =============================
exports.getChaptersByMangaAffichageDetail = async (req, res) => {
    try {
        const { mangaId } = req.params;

        //console.log('📚 getChaptersByManga appelé pour manga:', mangaId);

        // Vérifier que le manga existe
        const Manga = require('../models/Manga');
        const manga = await Manga.findById(mangaId);
        
        if (!manga) {
            console.log('❌ Manga non trouvé');
            return res.status(404).json({ message: 'Manga non trouvé' });
        }

        //console.log('✅ Manga trouvé:', manga.nom);

        // Récupérer les chapitres
        const chapters = await Chapter.find({ manga: mangaId })
            .sort({ chapterNumber: 1 });

        //console.log(`📖 ${chapters.length} chapitres trouvés`);

        // ✅ TOUJOURS retourner un OBJET wrapper
        const response = {
            message: chapters.length > 0 
                ? 'Chapitres récupérés avec succès' 
                : 'Aucun chapitre disponible',
            chapters: chapters,
            count: chapters.length,
            mangaId: mangaId
        };

        //console.log('✅ Retour de la réponse:', JSON.stringify(response, null, 2));

        res.json(response);

    } catch (error) {
        console.error('❌ Erreur getChaptersByManga:', error);
        res.status(500).json({ 
            message: 'Erreur serveur',
            error: error.message 
        });
    }
};
// =============================
// RÉCUPÉRER UN CHAPITRE PAR ID
// =============================
exports.getChapterById = async (req, res) => {
    try {
        const { id } = req.params;
        const { includePages = 'true', includeManga = 'false' } = req.query;
        
        let query = Chapter.findById(id);
        
        if (includeManga === 'true') {
            query = query.populate('manga', 'nom auteur urlImage');
        }
        
        const chapter = await query;
        
        if (!chapter) {
            return res.status(404).json({ message: 'Chapitre non trouvé' });
        }
        
        //  Trouver le chapitre précédent et suivant
        const previousChapter = await Chapter.findOne({
            manga: chapter.manga,
            chapterNumber: { $lt: chapter.chapterNumber }
        }).sort({ chapterNumber: -1 }).select('_id chapterNumber titre');
        
        const nextChapter = await Chapter.findOne({
            manga: chapter.manga,
            chapterNumber: { $gt: chapter.chapterNumber }
        }).sort({ chapterNumber: 1 }).select('_id chapterNumber titre');
        
        // Ne pas inclure les pages si non demandé
        if (includePages === 'false') {
            chapter.pages = undefined;
        }
        
        res.status(200).json({
            message: 'Chapitre trouvé',
            chapter: chapter,
            navigation: {
                previous: previousChapter,
                next: nextChapter
            }
        });
    } catch (err) {
        console.error('Erreur récupération chapitre:', err);
        
        if (err.name === 'CastError') {
            return res.status(400).json({ message: 'ID de chapitre invalide' });
        }
        
        res.status(500).json({ message: 'Erreur interne du serveur' });
    }
};
// =============================
// RÉCUPÉRER TOUS LES CHAPITRES
// =============================
exports.getAllChapters = async (req, res) => {
    try {
        const chapters = await Chapter.find()
            .populate('manga', 'nom couverture')
            .sort({ createdAt: -1 });

        res.json(chapters);
    } catch (error) {
        console.error('❌ Erreur récupération chapitres:', error);
        res.status(500).json({ 
            message: 'Erreur serveur',
            error: error.message 
        });
    }
};

// =============================
// METTRE À JOUR UN CHAPITRE
// =============================
exports.updateChapter = async (req, res) => {
    try {
        const { id } = req.params;
        const { titre, chapterNumber } = req.body;

        const chapter = await Chapter.findByIdAndUpdate(
            id,
            { titre, chapterNumber },
            { new: true, runValidators: true }
        );

        if (!chapter) {
            return res.status(404).json({ message: 'Chapitre non trouvé' });
        }

        // Envoyer une notification de mise à jour
        notifyChapterUpdated(chapter.manga.toString(), chapter.toObject());

        res.json(chapter);
    } catch (error) {
        console.error('❌ Erreur mise à jour chapitre:', error);
        res.status(500).json({ 
            message: 'Erreur serveur',
            error: error.message 
        });
    }
};

// =============================
// SUPPRIMER UN CHAPITRE
// =============================
exports.deleteChapter = async (req, res) => {
    try {
        const { id } = req.params;

        const chapter = await Chapter.findById(id);
        if (!chapter) {
            return res.status(404).json({ message: 'Chapitre non trouvé' });
        }

        // Supprimer toutes les images des pages
        chapter.pages.forEach(page => {
            if (page.urlImage) {
                const imagePath = path.join(__dirname, '../public', page.urlImage);
                if (fs.existsSync(imagePath)) {
                    fs.unlinkSync(imagePath);
                    console.log('🗑️ Image supprimée:', imagePath);
                }
            }
        });

        // Retirer le chapitre du manga
        await Manga.findByIdAndUpdate(
            chapter.manga,
            { $pull: { chapitres: id } }
        );

        // Supprimer le chapitre
        await Chapter.findByIdAndDelete(id);

        console.log('✅ Chapitre supprimé:', id);

        res.json({ message: 'Chapitre supprimé avec succès' });
    } catch (error) {
        console.error('❌ Erreur suppression chapitre:', error);
        res.status(500).json({ 
            message: 'Erreur serveur',
            error: error.message 
        });
    }
};