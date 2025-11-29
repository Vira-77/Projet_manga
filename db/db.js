const mongoose = require('mongoose');
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '..', '.env') });

const User = require('../models/User');
const Genre = require('../models/Genre');
const Manga = require('../models/Manga');
const Store = require('../models/Store');

let db_uri = process.env.MONGO_URL;

if (db_uri && db_uri.includes('localhost')) {
    db_uri = db_uri.replace(/localhost/g, '127.0.0.1');
}

async function connectToDatabase() {
    try {
        await mongoose.connect(db_uri);
        console.log('Connecté à MongoDB');
    } catch (err) {
        console.error('Erreur de connexion MongoDB:', err);
        process.exit(1);
    }
}

async function closeDatabaseConnection() {
    try {
        await mongoose.connection.close();
        console.log('Connexion MongoDB fermée');
    } catch (err) {
        console.error('Erreur lors de la fermeture:', err);
    }
}

async function addSampleData() {
    try {
        // Nettoyer la base
        console.log('Nettoyage de la base de données...');
        await Promise.all([
            User.deleteMany({}),
            Genre.deleteMany({}),
            Manga.deleteMany({}),
            Store.deleteMany({})
        ]);
        console.log('Collections vidées');

        // Créer des utilisateurs
        console.log('Création des utilisateurs...');
        const admin = await User.create({
            name: 'Admin',
            email: 'admin@manga.com',
            password: 'admin123',
            role: 'admin'
        });

        const adminManga = await User.create({
            name: 'Admin Manga',
            email: 'adminmanga@manga.com',
            password: 'admin123',
            role: 'admin_manga'
        });

        const user1 = await User.create({
            name: 'John Doe',
            email: 'john@example.com',
            password: 'password123',
            role: 'utilisateur',
            position: {
                type: 'Point',
                coordinates: [-71.9, 45.4] // Coordonnées de Sherbrooke
            }
        });

        // Créer des genres
        console.log('Création des genres...');
        const shonen = await Genre.create({
            name: 'Shonen',
            description: 'Manga destiné aux jeunes garçons'
        });

        const shojo = await Genre.create({
            name: 'Shojo',
            description: 'Manga destiné aux jeunes filles'
        });

        const seinen = await Genre.create({
            name: 'Seinen',
            description: 'Manga destiné aux jeunes adultes hommes'
        });

        // Créer des mangas
        console.log('Création des mangas...');
        const onePiece = await Manga.create({
            nom: 'One Piece',
            description: 'L\'aventure de Monkey D. Luffy et son équipage de pirates',
            auteur: 'Eiichiro Oda',
            dateDeSortie: new Date('1997-07-22'),
            urlImage: 'https://example.com/onepiece.jpg',
            genres: [shonen._id]
        });

        const naruto = await Manga.create({
            nom: 'Naruto',
            description: 'L\'histoire d\'un jeune ninja qui rêve de devenir Hokage',
            auteur: 'Masashi Kishimoto',
            dateDeSortie: new Date('1999-09-21'),
            urlImage: 'https://example.com/naruto.jpg',
            genres: [shonen._id]
        });

        // Mettre à jour les genres avec les mangas
        await Genre.updateMany(
            { _id: shonen._id },
            { $set: { mangas: [onePiece._id, naruto._id] } }
        );

        // Créer des magasins
        console.log('Création des magasins...');
        await Store.create({
            nom: 'Librairie Manga Plus',
            position: {
                type: 'Point',
                coordinates: [-71.9, 45.4] // Sherbrooke
            },
            adresse: '123 Rue Principale, Sherbrooke, QC'
        });

        await Store.create({
            nom: 'Manga Store Centre-Ville',
            position: {
                type: 'Point',
                coordinates: [-71.88, 45.38]
            },
            adresse: '456 Boulevard King, Sherbrooke, QC'
        });

        console.log('Données d\'échantillon ajoutées avec succès');
        console.log('\nRésumé:');
        console.log(`   - ${await User.countDocuments()} utilisateurs`);
        console.log(`   - ${await Genre.countDocuments()} genres`);
        console.log(`   - ${await Manga.countDocuments()} mangas`);
        console.log(`   - ${await Store.countDocuments()} magasins`);
        console.log('\nComptes créés:');
        console.log('   Admin: admin@manga.com / admin123');
        console.log('   Admin Manga: adminmanga@manga.com / admin123');
        console.log('   Utilisateur: john@example.com / password123');

    } catch (err) {
        console.error('Erreur lors de l\'ajout des données:', err);
    }
}
const connectDB = connectToDatabase;
module.exports = connectDB;

if (process.env.SEED_DB === 'true' || require.main === module) {
    (async () => {
        await connectToDatabase();
        await addSampleData();
        await closeDatabaseConnection();
    })();
}
