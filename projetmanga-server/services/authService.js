const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const User = require('../models/User');

// ==========================
//   INSCRIPTION 
// ==========================
exports.registerUser = async (name, email, password, role) => {

    if (!name || !email || !password) {
      throw new Error('Nom, email et mot de passe requis'); 
    }
  
  const existing = await User.findOne({ email });
  if (existing) {
    throw new Error('Un utilisateur avec cet email existe déjà');
  }

  // Création du nouvel utilisateur 
  const user = new User({ name, email, password, role: 'utilisateur' });
  await user.save();
  
  return user;
};

// ==========================
//   CONNEXION 
// ==========================
exports.loginUser = async (email, password) => {


  if (!email || !password) {
      throw new Error('Email et mot de passe requis');
  }
  
  const user = await User.findOne({ email });
  if (!user) {
    throw new Error('Utilisateur non trouvé');
  }

  // Utilisation de bcrypt pour comparer le mot de passe
  const isMatch = await user.comparePassword(password);
  if (!isMatch) {
    throw new Error('Mot de passe incorrect');
  }

  // Prépare le payload du token 
  const payload = {
    id: user._id,
    role: user.role, 
  };

  // Génération du token JWT
  const token = jwt.sign(
    payload,
    process.env.JWT_SECRET,
    { expiresIn: process.env.JWT_EXPIRES_IN || '3d' }
  );

  return { token, user };
};

