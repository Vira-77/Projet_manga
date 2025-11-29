const mongoose = require("mongoose");

//model utilise pour aider l'IA a mieux cibler les recommandations de manga en fonction des preferences utilisateur
const preferencesSchema = new mongoose.Schema({
    //prefernce de l'utilisateur userId
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    // pour le genre de manga genreId
    genreId: { type: mongoose.Schema.Types.ObjectId, ref: 'Genre', required: true },
    // avec un niveau d'age recent ou moins recent
    age : String,
});

module.exports = mongoose.model("Preferences", preferencesSchema);
