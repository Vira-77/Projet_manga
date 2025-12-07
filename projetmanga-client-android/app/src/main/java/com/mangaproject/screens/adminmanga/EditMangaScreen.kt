package com.mangaproject.screens.adminmanga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMangaScreen(
    mangaId: String,
    vm: EditMangaViewModel,
    onBack: () -> Unit
) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()
    val deleteSuccess by vm.deleteSuccess.collectAsState()
    val manga by vm.manga.collectAsState()
    val genres by vm.genres.collectAsState()
    val chapters by vm.chapters.collectAsState()

    var nom by remember { mutableStateOf("") }
    var auteur by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var selectedGenres by remember { mutableStateOf<List<String>>(emptyList()) }
    var genreMenuOpened by remember { mutableStateOf(false) }
    var newChapterText by remember { mutableStateOf("") }

    LaunchedEffect(mangaId) { vm.load(mangaId) }

    LaunchedEffect(manga) {
        manga?.let {
            nom = it.nom
            auteur = it.auteur
            description = it.description ?: ""
            date = it.dateDeSortie ?: ""
            imageUrl = it.urlImage ?: ""
            selectedGenres = it.genres ?: emptyList()
        }
    }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            vm.resetState()
            onBack()
        }
    }

    if (success) {
        AlertDialog(
            onDismissRequest = { vm.resetState() },
            title = { Text("Succès") },
            text = { Text("Le manga a été mis à jour") },
            confirmButton = {
                TextButton(onClick = { vm.resetState() }) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier un manga") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Image
            item {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = nom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            // Champs texte
            listOf(
                "Nom" to nom,
                "Auteur" to auteur,
                "Description" to description,
                "Date de sortie (YYYY-MM-DD)" to date,
                "URL image" to imageUrl
            ).forEachIndexed { i, pair ->
                item {
                    OutlinedTextField(
                        value = when(i) {
                            0 -> nom; 1 -> auteur; 2 -> description; 3 -> date; else -> imageUrl
                        },
                        onValueChange = {
                            when(i){
                                0 -> nom = it; 1 -> auteur = it; 2 -> description = it
                                3 -> date = it; else -> imageUrl = it
                            }
                        },
                        label = { Text(pair.first) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Genres
            item {
                ExposedDropdownMenuBox(
                    expanded = genreMenuOpened,
                    onExpandedChange = { genreMenuOpened = !genreMenuOpened }
                ) {
                    OutlinedTextField(
                        value = selectedGenres
                            .mapNotNull { id -> genres.firstOrNull { it.id == id }?.name }
                            .joinToString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Genres") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genreMenuOpened) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = genreMenuOpened,
                        onDismissRequest = { genreMenuOpened = false }
                    ) {
                        genres.forEach { genre ->
                            DropdownMenuItem(
                                text = { Text(genre.name) },
                                onClick = {
                                    selectedGenres = if (selectedGenres.contains(genre.id))
                                        selectedGenres - genre.id
                                    else
                                        selectedGenres + genre.id
                                }
                            )
                        }
                    }
                }
            }

            // ERROR
            item { if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error) }

            // ---- Chapitres ----
            item {
                OutlinedTextField(
                    value = newChapterText,
                    onValueChange = { newChapterText = it },
                    label = { Text("Ajouter un chapitre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        vm.addChapter(newChapterText)
                        newChapterText = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Ajouter le chapitre") }
            }

            items(chapters.size) { index ->
                val chapterUrl = chapters[index]
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    AsyncImage(
                        model = chapterUrl,
                        contentDescription = "Chapitre ${index + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    IconButton(onClick = { vm.removeChapter(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                    }
                }
            }


            // Bouton Update
            item {
                Button(
                    onClick = {
                        vm.update(
                            id = mangaId,
                            nom = nom,
                            auteur = auteur,
                            description = description,
                            date = date,
                            image = imageUrl,
                            genresIds = selectedGenres,
                            chapters = chapters
                        )
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (loading) "Mise à jour..." else "Enregistrer") }
            }

            // Bouton Supprimer
            item {
                Button(
                    onClick = { vm.delete(mangaId) },
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Supprimer", color = MaterialTheme.colorScheme.onError) }
            }
        }
    }
}
