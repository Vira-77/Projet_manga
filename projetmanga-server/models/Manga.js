const mongoose = require('mongoose');

const MangaSchema = new mongoose.Schema({
    nom: { type: String, required: true, trim: true },
    description: String,
    dateDeSortie: Date,
    urlImage: {type: String,default:'https://www.istockphoto.com/photos/placeholder-image'},
    auteur: { type: String, required: true, trim: true },

    genres: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Genre' }],
    chapitres: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Chapter' }],

    jikanId: { type: Number, default: null },
    source: { type: String, enum: ["local", "jikan"], default: "local" },

}, { timestamps: true });



module.exports = mongoose.model('Manga', MangaSchema);
