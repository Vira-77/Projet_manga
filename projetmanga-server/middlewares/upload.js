const multer = require('multer');
const path = require('path');
const fs = require('fs');

// ====================================
// CONFIGURATION POUR PROFIL
// ====================================
const profileStorage = multer.diskStorage({
    destination: (req, file, cb) => {
        const uploadPath = path.join(__dirname, '../public/uploads/profiles');
        if (!fs.existsSync(uploadPath)) {
            fs.mkdirSync(uploadPath, { recursive: true });
        }
        cb(null, uploadPath);
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, 'profile-' + uniqueSuffix + path.extname(file.originalname));
    }
});

// ====================================
// CONFIGURATION POUR PAGES DE CHAPITRES
// ====================================
const chapterPageStorage = multer.diskStorage({
    destination: (req, file, cb) => {
        const uploadPath = path.join(__dirname, '../public/uploads/chapters');
        if (!fs.existsSync(uploadPath)) {
            fs.mkdirSync(uploadPath, { recursive: true });
        }
        cb(null, uploadPath);
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, 'page-' + uniqueSuffix + path.extname(file.originalname));
    }
});

/*
// ====================================
// FILTRES DE FICHIERS
// ====================================
const imageFileFilter = (req, file, cb) => {
    const allowedTypes = /jpeg|jpg|png|gif|webp/;
    const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase());
    const mimetype = allowedTypes.test(file.mimetype);
    
    if (mimetype && extname) {
        return cb(null, true);
    } else {
        cb(new Error('Seules les images sont autorisées (jpeg, jpg, png, gif, webp)'));
    }
};*/

// ====================================
// INSTANCES MULTER
// ====================================

// Pour les photos de profil
const upload = multer({
    storage: profileStorage,
    limits: { fileSize: 5 * 1024 * 1024 }, // 5MB
    //fileFilter: imageFileFilter
});

// Pour les pages de chapitres
const uploadChapterPage = multer({
    storage: chapterPageStorage,
    limits: { fileSize: 10 * 1024 * 1024 }, // 10MB
    //fileFilter: imageFileFilter
});

module.exports = {
    upload,
    uploadChapterPage
};