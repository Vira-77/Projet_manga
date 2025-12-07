const mongoose = require('mongoose');
const path = require('path');

const User = require('../models/User');
const Genre = require('../models/Genre');
const Manga = require('../models/Manga');
const Store = require('../models/Store');
const Favorite = require('../models/Favorite');



let db_uri = process.env.MONGO_URI;

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
            Store.deleteMany({}),
            Favorite.deleteMany({})
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
                coordinates: [-71.9, 45.4] // Sherbrooke
            },
            jikanSync: true
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
            description: 'Mangas pour jeunes adultes, souvent plus sombres et complexes.'
        });

        const sport = await Genre.create({
            name: 'Sport',
            description: 'Mangas centrés sur la compétition sportive.'
        });

        const fantasy = await Genre.create({
            name: 'Fantasy',
            description: 'Mondes imaginaires, magie et créatures fantastiques.'
        });

        // ==========================
        //   MANGAS
        // ==========================
        console.log('Création des mangas...');

        const roboCat = await Manga.create({
            nom: 'Robo Cat',
            description: 'C’est un chat robot qui essaye de comprendre les humains mais il y arrive pas trop.',
            auteur: 'Tom Kenji',
            dateDeSortie: new Date('2020-03-12'),
            urlImage: 'https://example.com/robocat.jpg',
            genres: [shonen._id, fantasy._id],
            jikanId: null,
            source: 'local'
        });

        const bubbleQuest = await Manga.create({
            nom: 'Bubble Quest',
            description: 'Un garçon trouve une bulle magique qui parle. Ensemble ils vont dans plein de mondes bizarres.',
            auteur: 'Mina Yori',
            dateDeSortie: new Date('2018-06-04'),
            urlImage: 'https://example.com/bubblequest.jpg',
            genres: [shonen._id],
            jikanId: null,
            source: 'local'
        });

        const cookingSlime = await Manga.create({
            nom: 'Cooking Slime',
            description: 'Un slime qui veut devenir chef cuisinier même si personne veut manger ce qu’il fait.',
            auteur: 'Riku Han',
            dateDeSortie: new Date('2021-11-22'),
            urlImage: 'https://example.com/cookingslime.jpg',
            genres: [shonen._id, fantasy._id],
            jikanId: null,
            source: 'local'
        });

        const moonLetters = await Manga.create({
            nom: 'Moon Letters',
            description: 'Deux jeunes s’envoient des lettres qu’ils laissent sur la lune (oui c’est pas très logique).',
            auteur: 'Sora Miho',
            dateDeSortie: new Date('2019-09-10'),
            urlImage: 'https://example.com/moonletters.jpg',
            genres: [shojo._id],
            jikanId: null,
            source: 'local'
        });

        const steelShadow = await Manga.create({
            nom: 'Steel Shadow',
            description: 'Un mercenaire avec un bras en métal se bat contre des bandits mais il comprend jamais ce qu’ils veulent.',
            auteur: 'Akira Shidou',
            dateDeSortie: new Date('2014-01-17'),
            urlImage: 'https://example.com/steelshadow.jpg',
            genres: [seinen._id, fantasy._id],
            jikanId: null,
            source: 'local'
        });

        const volleyDreams = await Manga.create({
            nom: 'Volley Dreams',
            description: 'Un petit club de volley qui perd tout le temps mais qui essaye quand même de faire mieux.',
            auteur: 'Yuto Kai',
            dateDeSortie: new Date('2017-05-30'),
            urlImage: 'https://example.com/volleydreams.jpg',
            genres: [shonen._id, sport._id],
            jikanId: null,
            source: 'local'
        });

        //lier les mangas aux genres
        console.log(' Mise à jour des genres avec les mangas...');

        await Genre.findByIdAndUpdate(shonen._id, {
            mangas: [roboCat._id, bubbleQuest._id, cookingSlime._id, volleyDreams._id]
        });

        await Genre.findByIdAndUpdate(shojo._id, {
            mangas: [moonLetters._id]
        });

        await Genre.findByIdAndUpdate(seinen._id, {
            mangas: [steelShadow._id]
        });

        await Genre.findByIdAndUpdate(sport._id, {
            mangas: [volleyDreams._id]
        });

        await Genre.findByIdAndUpdate(fantasy._id, {
            mangas: [roboCat._id, cookingSlime._id, steelShadow._id]
        });

        // ==========================
        //   FAVORIS UTILISATEUR 
        // ==========================
        console.log('Ajout des favoris pour John Doe ');

        user1.favorites = [roboCat._id, bubbleQuest._id, cookingSlime._id];
        await user1.save();

        // ==========================
        //   FAVORIS UTILISATEUR (nouveau système Favorite)
        // ==========================
        console.log('Ajout des favoris dans le modèle Favorite...');

        // Favoris pour John Doe (utilisateur normal)
        await Favorite.create({
            user: user1._id,
            mangaId: roboCat._id.toString(),
            source: 'local',
            title: roboCat.nom,
            imageUrl: roboCat.urlImage
        });

        await Favorite.create({
            user: user1._id,
            mangaId: bubbleQuest._id.toString(),
            source: 'local',
            title: bubbleQuest.nom,
            imageUrl: bubbleQuest.urlImage
        });

        await Favorite.create({
            user: user1._id,
            mangaId: cookingSlime._id.toString(),
            source: 'local',
            title: cookingSlime.nom,
            imageUrl: cookingSlime.urlImage
        });

        await Favorite.create({
            user: user1._id,
            mangaId: moonLetters._id.toString(),
            source: 'local',
            title: moonLetters.nom,
            imageUrl: moonLetters.urlImage
        });

        // Favoris pour Admin (pour tester)
        await Favorite.create({
            user: admin._id,
            mangaId: steelShadow._id.toString(),
            source: 'local',
            title: steelShadow.nom,
            imageUrl: steelShadow.urlImage
        });

        await Favorite.create({
            user: admin._id,
            mangaId: volleyDreams._id.toString(),
            source: 'local',
            title: volleyDreams.nom,
            imageUrl: volleyDreams.urlImage
        });

        // Favoris pour Admin Manga (pour tester)
        await Favorite.create({
            user: adminManga._id,
            mangaId: roboCat._id.toString(),
            source: 'local',
            title: roboCat.nom,
            imageUrl: roboCat.urlImage
        });


        //création des magasins
        console.log('Création des magasins...');

        // Créer des magasins
        console.log('Création des magasins...');
        await Store.create({
            nom: 'Librairie Manga Plus',
            position: {
                type: 'Point',
                coordinates: [-71.9, 45.4] // Sherbrooke
            },
            adresse: '123 Rue Principale, Sherbrooke, QC',
            telephone: '+1 819-555-0101',
            email: 'contact@mangaplus.ca',
            horaires: 'Lun-Sam 10h-19h'
        });

        await Store.create({
            nom: 'Manga Store Centre-Ville',
            position: {
                type: 'Point',
                coordinates: [-71.88, 45.38]
            },
            adresse: '456 Boulevard King, Sherbrooke, QC',
            telephone: '+1 819-555-0202',
            email: 'info@mangastore-centre.ca',
            horaires: 'Tous les jours 11h-21h'
        });

        console.log('Données d\'échantillon ajoutées avec succès');
        console.log('\nRésumé:');
        console.log(`   - ${await User.countDocuments()} utilisateurs`);
        console.log(`   - ${await Genre.countDocuments()} genres`);
        console.log(`   - ${await Manga.countDocuments()} mangas`);
        console.log(`   - ${await Store.countDocuments()} magasins`);
        console.log(`   - ${await Favorite.countDocuments()} favoris`);
        console.log('\nComptes créés (mots de passe AVANT hash) :');
        console.log('   Admin:       admin@manga.com / admin123');
        console.log('   Admin Manga: adminmanga@manga.com / admin123');
        console.log('   Utilisateur: john@example.com / password123');
        console.log('\nFavoris créés:');
        console.log('   John Doe: Robo Cat, Bubble Quest, Cooking Slime, Moon Letters');
        console.log('   Admin: Steel Shadow, Volley Dreams');
        console.log('   Admin Manga: Robo Cat');

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
