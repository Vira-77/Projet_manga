const mongoose = require('mongoose');
const bcrypt = require('bcrypt');

const UserSchema = new mongoose.Schema({
    name: { type: String, required: true, trim: true },
    email: { type: String, unique: true, required: true, trim: true },
    password: { type: String, required: true },
    phone: String,
    address: String,

    role: {
        type: String,
        enum: ['utilisateur', 'admin_manga', 'admin'],
        default: 'utilisateur'
    },

    // champ pour la géolocalisation
    position: {
        type: {
            type: String,
            enum: ['Point'],
            default: 'Point'
        },
        coordinates: {
            type: [Number],
            required: false
        }
    },

    // Favoris du user
    favorites: [
        { type: mongoose.Schema.Types.ObjectId, ref: 'Manga' }
    ],
}, {
  toJSON: {
    // Supprime le mot de passe des réponses JSON
    transform(doc, ret) {
      delete ret.password;
      return ret;
    }
  }
});

    // Pour savoir si Android a synchronisé les données locales
    jikanSync: { type: Boolean, default: false },

}, { timestamps: true });

// Hachage du mot de passe avant enregistrement
UserSchema.pre('save', async function () {
    if (!this.isModified('password')) return;
    this.password = await bcrypt.hash(this.password, 10);
});

UserSchema.methods.comparePassword = function (plaintext) {
    return bcrypt.compare(plaintext, this.password);
};


module.exports = mongoose.model('User', UserSchema);
