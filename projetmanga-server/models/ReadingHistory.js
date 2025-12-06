const mongoose = require('mongoose');

const ReadingHistorySchema = new mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true,
        index: true
    },
    mangaId: {
        type: String,
        required: true
    },
    source: {
        type: String,
        enum: ['local', 'jikan'],
        required: true
    },
    currentChapterId: {
        type: String,
        required: false
    },
    currentChapterNumber: {
        type: Number,
        required: false
    },
    lastReadAt: {
        type: Date,
        default: Date.now
    },
    title: String,
    imageUrl: String
}, { timestamps: true });

ReadingHistorySchema.index({ user: 1, mangaId: 1 }, { unique: true });

module.exports = mongoose.model('ReadingHistory', ReadingHistorySchema);

