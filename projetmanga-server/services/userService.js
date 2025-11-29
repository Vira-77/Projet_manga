const User = require('../models/User');

// Logique pour créer un utilisateur (C - Create)
const createUser = async (userData) => {
    const user = new User(userData);
    return await user.save();
};

// Logique pour récupérer tous les utilisateurs 
const getAllUsers = async (query = {}) => {
    return await User.find(query);
};

// Logique pour récupérer un utilisateur par ID 
const getUserById = async (userId) => {
    return await User.findById(userId);
};

// Logique pour mettre à jour un utilisateur 
const updateUser = async (userId, updateData) => {
    const user = await User.findByIdAndUpdate(
        userId, 
        updateData, 
        { new: true, runValidators: true }
    );
    return user;
};

// Logique pour supprimer un utilisateur 
const deleteUser = async (userId) => {
    return await User.findByIdAndDelete(userId);
};

module.exports = {
    createUser,
    getAllUsers,
    getUserById,
    updateUser,
    deleteUser
};