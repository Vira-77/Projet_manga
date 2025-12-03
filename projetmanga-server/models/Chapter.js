const mongoose = require('mongoose');


const PageSchema = new mongoose.Schema({
    numero: { 
        type: Number, 
        required: true 
    },
    urlImage: { 
        type: String, 
        required: true 
    }
}, { _id: false });

const ChapterSchema = new mongoose.Schema({
    titre: { 
        type: String, 
        required: true 
    },
    // Référence au Manga auquel appartient ce Chapitre
    manga: { 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Manga', 
        required: true 
    },
    pages: [PageSchema] 
});

module.exports = mongoose.model('Chapter', ChapterSchema); 
