const mongoose = require('mongoose');

const GenreSchema = new mongoose.Schema({
    name: { type: String, required: true, unique: true, trim: true },
    description: String,
    mangas: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Manga' }]
}, { timestamps: true });

module.exports = mongoose.model('Genre', GenreSchema);
