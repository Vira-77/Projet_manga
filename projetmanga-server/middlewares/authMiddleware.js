const jwt = require('jsonwebtoken');
const User = require('../models/User');

const verifyToken = async (req, res, next) => {
  try {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ message: 'Accès refusé : token manquant.' });
    }

    const token = authHeader.split(' ')[1];
    const decoded = jwt.verify(token, process.env.JWT_SECRET);

    let user = await User.findById(decoded.id).select('-password');
    

    if (!user) {
      return res.status(404).json({ message: 'Utilisateur non trouvé.' });
    }

    // Ajoute les infos utilisateur dans la requête
    req.user = {
      id: user.id,
      role: user.role, 
    };

    //console.log("token verified")
    next();
  } catch (err) {
    console.error('Erreur verifyToken:', err.message);
    res.status(401).json({ message: 'Token invalide ou expiré.' });
  }
};

// Vérifie les rôles autorisés
const authorizeRoles = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({ message: 'Utilisateur non authentifié.' });
    }
    if (!allowedRoles.includes(req.user.role)) {
      return res.status(403).json({
        message: `Accès interdit : rôle '${req.user.role}' non autorisé.`
      });
    }
    next();
  };
};

module.exports = {
  verifyToken,
  authorizeRoles
};
