const mongoose = require('mongoose');
const bcrypt = require('bcrypt');

const UserSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, unique: true, required: true },
  password: { type: String, required: true },
  phone: String,
  address: String,

  // Rôle de l'utilisateur : Utilisateur,admin_manga,admin
  role: {
    type: String,
    enum: ['utilisateur', 'admin_manga', 'admin'],
    default: 'utilisateur'
  },

  // Champ pour la géolocalisation
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
  }

}, {
  toJSON: {
    // Supprime le mot de passe des réponses JSON
    transform(doc, ret) {
      delete ret.password;
      return ret;
    }
  }
});


// Hachage du mot de passe avant enregistrement
UserSchema.pre('save', async function () {
  if (!this.isModified('password')) return ;
    const saltRounds = 10;
    const hash = await bcrypt.hash(this.password, saltRounds);
    this.password = hash;
});

// Comparer un mot de passe en clair avec le haché
UserSchema.methods.comparePassword = function (candidate) {
  return bcrypt.compare(candidate, this.password);
};


module.exports = mongoose.model('User', UserSchema);
