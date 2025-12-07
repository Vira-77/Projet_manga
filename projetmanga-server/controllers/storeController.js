const storeService = require('../services/storeService');

// ==========================
//   CRÉATION D'UN MAGASIN
// ==========================
exports.createStore = async (req, res) => {
    try {
        const { nom, adresse, telephone, email, horaires, coordinates } = req.body;
        
        const storeData = {
            nom,
            adresse,
            telephone,
            email,
            horaires,
            coordinates,
        };

        const newStore = await storeService.createStore(storeData);
        
        res.status(201).json({
            message: 'Magasin créé avec succès',
            store: newStore
        });

    } catch (err) {
        console.error('Erreur création magasin:', err);
        res.status(400).json({ 
            message: err.message || 'Erreur lors de la création du magasin' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION DE TOUS LES MAGASINS
// ==========================
exports.getAllStores = async (req, res) => {
    try {
        const stores = await storeService.getAllStores();
        
        res.status(200).json({
            message: 'Magasins récupérés avec succès',
            stores,
            count: stores.length
        });
    } catch (err) {
        console.error('Erreur récupération magasins:', err);
        res.status(500).json({ 
            message: 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   RÉCUPÉRATION D'UN MAGASIN PAR ID
// ==========================
exports.getStoreById = async (req, res) => {
    try {
        const { id } = req.params;
        const store = await storeService.getStoreById(id);
        
        res.status(200).json({
            message: 'Magasin trouvé',
            store
        });
    } catch (err) {
        console.error('Erreur récupération magasin:', err);
        const statusCode = err.message === 'Magasin non trouvé' ? 404 : 500;
        res.status(statusCode).json({ 
            message: err.message || 'Erreur interne du serveur' 
        });
    }
};

// ==========================
//   MISE À JOUR D'UN MAGASIN
// ==========================
exports.updateStore = async (req, res) => {
    try {
        const { id } = req.params;
        const updateData = req.body;
        
        const updatedStore = await storeService.updateStore(id, updateData);
        
        res.status(200).json({
            message: 'Magasin mis à jour avec succès',
            store: updatedStore
        });
    } catch (err) {
        console.error('Erreur mise à jour magasin:', err);
        const statusCode = err.message === 'Magasin non trouvé' ? 404 : 400;
        res.status(statusCode).json({ 
            message: err.message || 'Erreur lors de la mise à jour' 
        });
    }
};

// ==========================
//   SUPPRESSION D'UN MAGASIN
// ==========================
exports.deleteStore = async (req, res) => {
    try {
        const { id } = req.params;
        const deletedStore = await storeService.deleteStore(id);
        
        res.status(200).json({
            message: 'Magasin supprimé avec succès',
            store: deletedStore
        });
    } catch (err) {
        console.error('Erreur suppression magasin:', err);
        const statusCode = err.message === 'Magasin non trouvé' ? 404 : 500;
        res.status(statusCode).json({ 
            message: err.message || 'Erreur interne du serveur' 
        });
    }
};

