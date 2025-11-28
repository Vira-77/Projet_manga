const authService = require('../services/authService');

// ==========================
//   INSCRIPTION 
// ==========================
exports.register = async (req, res) => {
  try {
    const { name, email, password, role } = req.body;


    const newUser = await authService.registerUser(name, email, password, role);
    
    // Réponse de succès
    res.status(201).json({
      message: 'Utilisateur créé avec succès',
      user: {
        id: newUser._id,
        name: newUser.name,
        email: newUser.email,
        role: newUser.role
      }
    });

  } catch (err) {
    // Gestion des erreurs spécifiques renvoyées par le service
    if (err.message === 'Nom, email et mot de passe requis' || 
        err.message === 'Un utilisateur avec cet email existe déjà') {
      return res.status(400).json({ error: err.message });
    }
    console.error("ERREUR CRITIQUE D'INSCRIPTION (500) :", err);
    res.status(500).json({ error: 'Erreur interne du serveur lors de l\'inscription' });
  }
};

// ==========================
//   CONNEXION 
// ==========================
exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;


    const { token, user } = await authService.loginUser(email, password);

    // Réponse de succès
    res.status(200).json({
      message: 'Connexion réussie',
      token,
      user: {
        id: user._id,
        name: user.name,
        email: user.email,
        role: user.role,
      }
    });

  } catch (err) {
    // Gestion des erreurs spécifiques
    if (err.message === 'Utilisateur non trouvé' || err.message === 'Mot de passe incorrect'||
        err.message==='Email et mot de passe requis') {
      return res.status(400).json({ error: err.message });
    }
    res.status(500).json({ error: 'Erreur interne du serveur lors de la connexion' });
  }
};