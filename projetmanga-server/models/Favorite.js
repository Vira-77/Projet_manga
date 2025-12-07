const mongoose = require('mongoose');

const FavoriteSchema = new mongoose.Schema({
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
    title: String,
    imageUrl: String,
    createdAt: { type: Date, default: Date.now }
});

FavoriteSchema.index({ user: 1, mangaId: 1 }, { unique: true });

module.exports = mongoose.model('Favorite', FavoriteSchema);
