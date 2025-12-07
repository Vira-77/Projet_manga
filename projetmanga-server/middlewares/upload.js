const multer = require('multer');
const path = require('path');
const fs = require('fs');

const createUploadDirs = () => {
    const dirs = [
        './public/uploads/profiles',
        './public/uploads/mangas',
        './public/uploads/chapters',
        './public/uploads/stores'
    ];
    
    dirs.forEach(dir => {
        if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir, { recursive: true });
            console.log(`✅ Dossier créé: ${dir}`);
        }
    });
};

createUploadDirs();

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        let uploadPath = './public/uploads/';
        
        if (req.path.includes('profile')) {
            uploadPath += 'profiles';
        } else if (req.path.includes('manga')) {
            uploadPath += 'mangas';
        } else if (req.path.includes('chapter')) {
            uploadPath += 'chapters';
        } else if (req.path.includes('store')) {
            uploadPath += 'stores';
        } else {
            uploadPath += 'profiles'; // Par défaut
        }
        
        cb(null, uploadPath);
    },
    filename: function (req, file, cb) {
        // Générer un nom unique
        const userId = req.user?.id || 'guest';
        const timestamp = Date.now();
        const randomString = Math.random().toString(36).substring(2, 8);
        const ext = path.extname(file.originalname);
        
        const filename = `${userId}_${timestamp}_${randomString}${ext}`;
        cb(null, filename);
    }
});

const fileFilter = (req, file, cb) => {
    if (file.mimetype.startsWith('image/')) {
        console.log('✅ Image acceptée:', file.mimetype);
        cb(null, true);
    } else {
        console.log('❌ Pas une image:', file.mimetype);
        cb(new Error('Le fichier doit être une image.'), false);
    }
};

// Configuration Multer
const upload = multer({
    storage: storage,
    limits: {
        fileSize: 5 * 1024 * 1024 // 5MB max
    },
    fileFilter: fileFilter
});

// Fonction helper pour supprimer une image
const deleteImage = (imagePath) => {
    if (!imagePath) return;
    
    const fullPath = path.join(__dirname, '..', 'public', imagePath);
    
    if (fs.existsSync(fullPath)) {
        fs.unlinkSync(fullPath);
        console.log(`🗑️ Image supprimée: ${imagePath}`);
    }
};

module.exports = { upload, deleteImage };