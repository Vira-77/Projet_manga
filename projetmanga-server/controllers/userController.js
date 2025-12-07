const userService = require('../services/userService');
const { deleteImage } = require('../middlewares/upload');
const User = require('../models/User');

exports.uploadProfilePicture = async (req, res) => {
    try {

        console.log('📸 Upload demandé');
        console.log('🔑 req.user:', req.user);
        console.log('📦 req.file:', req.file);
        console.log('🎫 Authorization header:', req.headers['authorization']);
        if (!req.file) {
            return res.status(400).json({ message: 'Aucun fichier envoyé' });
        }

        const userId = req.user.id;
        
        // Construire le chemin relatif (stocké en BDD)
        const relativePath = `/uploads/profiles/${req.file.filename}`;

        const user = await User.findById(userId);
        
        if (!user) {
            // Supprimer le fichier uploadé si l'utilisateur n'existe pas
            deleteImage(relativePath);
            return res.status(404).json({ message: 'Utilisateur non trouvé' });
        }

        // Supprimer l'ancienne photo si elle existe
        if (user.profilePicture) {
            deleteImage(user.profilePicture);
        }

        //  Mettre à jour avec le chemin relatif
        user.profilePicture = relativePath;
        await user.save();

        console.log(`Photo de profil mise à jour: ${relativePath}`);

        res.status(200).json({
            message: 'Photo de profil mise à jour avec succès',
            profilePicture: relativePath,
            user: {
                id: user._id,
                name: user.name,
                email: user.email,
                profilePicture: user.profilePicture
            }
        });

    } catch (err) {
        console.error('Erreur upload photo de profil:', err);
        
        // Supprimer le fichier en cas d'erreur
        if (req.file) {
            deleteImage(`/uploads/profiles/${req.file.filename}`);
        }
        
        res.status(500).json({ 
            message: 'Erreur interne du serveur',
            error: err.message 
        });
    }
};

exports.deleteProfilePicture = async (req, res) => {
    try {
        const userId = req.user.id;
        const user = await User.findById(userId).select('-password');

        if (!user) {
            return res.status(404).json({ message: 'Utilisateur non trouvé' });
        }

        if (user.profilePicture) {
            deleteImage(user.profilePicture);
            user.profilePicture = null;
            await user.save();
        }

        res.status(200).json({
            message: 'Photo de profil supprimée',
            user: {
                id: user._id,
                name: user.name,
                email: user.email,
                profilePicture: user.profilePicture,
                bio: user.bio
            }
        });

    } catch (err) {
        console.error('❌ Erreur suppression photo:', err);
        res.status(500).json({ message: 'Erreur interne du serveur' });
    }
};


// POST /users
const createUserController = async (req, res) => {
    try {
        const newUser = await userService.createUser(req.body);
        res.status(201).json({ 
            message: 'Utilisateur créé avec succès', 
            user: newUser 
        });
    } catch (error) {
        if (error.code === 11000) { // Erreur de clé unique (doublon d'email)
            return res.status(409).json({ message: 'Cet email est déjà utilisé.' });
        }
        res.status(400).json({ message: 'Erreur lors de la création de l\'utilisateur', error: error.message });
    }
};

// GET /users
const getAllUsersController = async (req, res) => {
    try {
        const users = await userService.getAllUsers(req.query);
        res.status(200).json(users);
    } catch (error) {
        res.status(500).json({ message: 'Erreur lors de la récupération des utilisateurs', error: error.message });
    }
};

// GET /users/:id
const getUserByIdController = async (req, res) => {
    try {
        const user = await userService.getUserById(req.params.id);
        if (!user) {
            return res.status(404).json({ message: 'Utilisateur non trouvé' });
        }
        res.status(200).json(user);
    } catch (error) {
        if (error.name === 'CastError') {
             return res.status(400).json({ message: 'ID utilisateur invalide.' });
        }
        res.status(500).json({ message: 'Erreur lors de la récupération de l\'utilisateur', error: error.message });
    }
};

// PUT /users/:id (ou PATCH)
const updateUserController = async (req, res) => {
    try {
        const updatedUser = await userService.updateUser(req.params.id, req.body);
        if (!updatedUser) {
            return res.status(404).json({ message: 'Utilisateur non trouvé' });
        }
        res.status(200).json({ 
            message: 'Utilisateur mis à jour avec succès', 
            user: updatedUser 
        });
    } catch (error) {
        if (error.name === 'CastError') {
             return res.status(400).json({ message: 'ID utilisateur invalide.' });
        }
        // Gérer les erreurs de validation ou de doublons lors de la mise à jour
        res.status(400).json({ message: 'Erreur lors de la mise à jour de l\'utilisateur', error: error.message });
    }
};

// DELETE /users/:id
const deleteUserController = async (req, res) => {
    try {
        const deletedUser = await userService.deleteUser(req.params.id);
        if (!deletedUser) {
            return res.status(404).json({ message: 'Utilisateur non trouvé' });
        }
        res.status(200).json({ message: 'Utilisateur supprimé avec succès' });
    } catch (error) {
        if (error.name === 'CastError') {
             return res.status(400).json({ message: 'ID utilisateur invalide.' });
        }
        res.status(500).json({ message: 'Erreur lors de la suppression de l\'utilisateur', error: error.message });
    }
};

module.exports = {
    createUserController,
    getAllUsersController,
    getUserByIdController,
    updateUserController,
    deleteUserController,
    uploadProfilePicture: exports.uploadProfilePicture, 
    deleteProfilePicture: exports.deleteProfilePicture
};