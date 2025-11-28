const mongoose = require('mongoose');


const GenreSchema = new mongoose.Schema({
    name: { 
        type: String, 
        required: true, 
        unique: true ,
        trim:true
    },
    description: { 
        type: String ,
        trim:true
    },
    // Liste des références aux IDs des Mangas de ce genre
    mangas: [{ 
        type: mongoose.Schema.Types.ObjectId, 
        ref: 'Manga' 
    }]
});


module.exports = mongoose.model('Genre', GenreSchema); 