const mongoose = require('mongoose');


const PageSchema = new mongoose.Schema({
    numero: { type: Number, required: true },
    urlImage: { type: String, required: true }
}, { _id: false });

const ChapterSchema = new mongoose.Schema({
    titre: { type: String, required: true },
    manga: { type: mongoose.Schema.Types.ObjectId, ref: 'Manga', required: true },

    pages: [PageSchema],

    chapterNumber: { type: Number, default: null },
    jikanChapterId: { type: Number, default: null }

}, { timestamps: true });

module.exports = mongoose.model('Chapter', ChapterSchema);
