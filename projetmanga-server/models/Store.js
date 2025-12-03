const mongoose = require('mongoose');

const StoreSchema = new mongoose.Schema({
    nom: { type: String, required: true, trim: true },
    adresse: String,
    telephone: String,
    email: String,
    horaires: String,

    position: {
        type: { type: String, enum: ['Point'], default: 'Point' },
        coordinates: { type: [Number], required: true }
    }

}, { timestamps: true });

StoreSchema.index({ position: "2dsphere" });

module.exports = mongoose.model('Store', StoreSchema);
