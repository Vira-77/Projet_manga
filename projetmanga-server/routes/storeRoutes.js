const express = require('express');
const router = express.Router();

// Importation des contrôleurs et middlewares
const storeController = require('../controllers/storeController');
const { verifyToken, authorizeRoles } = require('../middlewares/authMiddleware'); 

// ==========================
//   ROUTES PUBLIQUES
// ==========================

// Récupérer tous les magasins 
router.get('/', storeController.getAllStores);

// Récupérer un magasin par ID 
router.get('/:id', storeController.getStoreById);



// ==========================
//   ROUTES PROTÉGÉES
// ==========================

// Créer un magasin (admin seulement)
router.post(
    '/', 
    verifyToken, 
    authorizeRoles('admin'), 
    storeController.createStore
);

// Mettre à jour un magasin par ID (admin seulement)
router.put(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin'), 
    storeController.updateStore
);

// Supprimer un magasin par ID (admin seulement)
router.delete(
    '/:id', 
    verifyToken, 
    authorizeRoles('admin'), 
    storeController.deleteStore
);

module.exports = router;