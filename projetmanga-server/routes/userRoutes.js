const express = require('express');
const router = express.Router();

// Importation des contrôleurs et middlewares
const userController = require('../controllers/userController');
const { verifyToken, authorizeRoles } = require('../middlewares/authMiddleware'); 
const { upload } = require('../middlewares/upload');



// Route pour uploader la photo de profil
router.post(
    '/profile/picture',
    verifyToken,
    authorizeRoles('admin', 'admin_manga', 'utilisateur'),
    upload.single('profilePicture'), // 'profilePicture' = nom du champ
    userController.uploadProfilePicture
);

// Route pour supprimer la photo de profil
router.delete(
    '/profile/picture',
    verifyToken,
    authorizeRoles('admin', 'admin_manga', 'utilisateur'),
    userController.deleteProfilePicture
);

// Récupérer un utilisateur par ID
router.get(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin','admin_manga','utilisateur'), 
    userController.getUserByIdController
);


// Mettre à jour un utilisateur par ID
router.put(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin','admin_manga','utilisateur'), 
    userController.updateUserController
);



// ===================================
// CRUD ADMINISTRATIF (pour les admins) 
// ===================================

// Créer un utilisateur 
// Seuls les admins peuvent faire cette opération
router.post(
    '/', 
    verifyToken, 
    authorizeRoles('admin'), 
    userController.createUserController
);

// Récupérer tous les utilisateurs
router.get(
    '/', 
    verifyToken, 
    authorizeRoles('admin', 'admin_manga'), 
    userController.getAllUsersController
);


// Supprimer un utilisateur par ID
router.delete(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin'), 
    userController.deleteUserController
);

module.exports = router;