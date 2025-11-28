const mongoose = require('mongoose');

const MangaSchema = new mongoose.Schema({
    nom: { 
        type: String, 
        required: true 
    },
    description: { 
        type: String 
    },
    dateDeSortie: { 
        type: Date, 
    },
    urlImage: { 
        type: String 
    },
     auteur: { 
        type: String, 
        required: true,
        trim: true
    },
    genres: [{ 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Genre' 
    }],
    chapitres: [{ 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Chapter' 
    }]
});

module.exports = mongoose.model('Manga', MangaSchema); 
