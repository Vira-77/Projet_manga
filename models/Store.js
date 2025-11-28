const mongoose = require('mongoose');

const StoreSchema = new mongoose.Schema({
    nom: { 
        type: String, 
        required: true 
    },
    adresse: {
        type: String,
        required: false
    },
    telephone: {
        type: String,
        required: false
    },
    email: {
        type: String,
        required: false
    },
    horaires: {
        type: String,
        required: false
    },
    // Position gérée par l'index GeoJSON 
    position: {
        type: {
            type: String,
            enum: ['Point'],
            default: 'Point'
        },
        coordinates: {
            type: [Number], // [longitude, latitude]
            required: true
        }
    }
});

// Création d'un index 2dsphere pour la géolocalisation
StoreSchema.index({ position: '2dsphere' });

module.exports = mongoose.model('Store', StoreSchema);