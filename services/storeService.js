const Store = require('../models/Store');

// ==========================
//   CRÉATION D'UN MAGASIN
// ==========================
exports.createStore = async (storeData) => {
    const { nom, adresse, telephone, email, horaires, coordinates} = storeData;
    
    if (!nom || !coordinates || coordinates.length !== 2) {
        throw new Error('Nom et coordonnées [longitude, latitude] requis');
    }

    // Validation des coordonnées
    const [longitude, latitude] = coordinates;
    if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
        throw new Error('Coordonnées invalides');
    }

    const store = new Store({
        nom,
        adresse,
        telephone,
        email,
        horaires,
        position: {
            type: 'Point',
            coordinates: [parseFloat(longitude), parseFloat(latitude)]
        },
    });

    await store.save();
    return store;
};

// ==========================
//   RÉCUPÉRATION DE TOUS LES MAGASINS
// ==========================
exports.getAllStores = async (filters = {}) => {
    const stores = await Store.find(filters)
        .populate('nom email')
        .sort({ createdAt: -1 });
    
    return stores;
};

// ==========================
//   RÉCUPÉRATION D'UN MAGASIN PAR ID
// ==========================
exports.getStoreById = async (id) => {
    const store = await Store.findById(id).populate('nom email');
    
    if (!store) {
        throw new Error('Magasin non trouvé');
    }
    
    return store;
};

// ==========================
//   MISE À JOUR D'UN MAGASIN
// ==========================
exports.updateStore = async (id, updateData) => {
    const { coordinates, ...otherData } = updateData;
    
    // Si des coordonnées sont fournies, les valider
    if (coordinates && coordinates.length === 2) {
        const [longitude, latitude] = coordinates;
        if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
            throw new Error('Coordonnées invalides');
        }
        
        otherData.position = {
            type: 'Point',
            coordinates: [parseFloat(longitude), parseFloat(latitude)]
        };
    }

    const store = await Store.findByIdAndUpdate(
        id, 
        otherData, 
        { new: true, runValidators: true }
    ).populate('nom email');
    
    if (!store) {
        throw new Error('Magasin non trouvé');
    }
    
    return store;
};

// ==========================
//   SUPPRESSION D'UN MAGASIN
// ==========================
exports.deleteStore = async (id) => {
    const store = await Store.findByIdAndDelete(id);
    
    if (!store) {
        throw new Error('Magasin non trouvé');
    }
    
    return store;
};

