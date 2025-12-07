package com.mangaproject.screens.adminmanga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.model.Genre
import com.mangaproject.utils.ImageUtils.toFullImageUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMangaScreen(
    mangaId: String,
    vm: EditMangaViewModel,
    onBack: () -> Unit,
    onNavigateToCreateChapter: (String) -> Unit
) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()
    val deleteSuccess by vm.deleteSuccess.collectAsState()
    val manga by vm.manga.collectAsState()
    val genres by vm.genres.collectAsState()

    var nom by remember { mutableStateOf("") }
    var auteur by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var selectedGenres by remember { mutableStateOf<List<String>>(emptyList()) }
    var genreMenuOpened by remember { mutableStateOf(false) }

    LaunchedEffect(mangaId) {
        vm.load(mangaId)
    }

    // Une fois le manga récupéré → remplir les champs
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

    // Quand suppression réussie → retour arrière
    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            vm.resetState()
            onBack()
        }
    }

    // Popup succés
    if (success) {
        AlertDialog(
            onDismissRequest = { vm.resetState() },
            title = { Text("Succès") },
            text = { Text("Le manga a été mis à jour") },
            confirmButton = {
                TextButton(onClick = { vm.resetState() }) {
                    Text("OK")
                }
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

            // IMAGE
            item {
                AsyncImage(
                    model = imageUrl.toFullImageUrl(),
                    contentDescription = nom,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            }

            // CHAMPS TEXTE
            item {
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom du manga") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = auteur,
                    onValueChange = { auteur = it },
                    label = { Text("Auteur") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date de sortie (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL image") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // GENRES
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
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
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
            item {
                if (error != null)
                    Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            // BOUTON UPDATE
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
                            genresIds = selectedGenres
                        )
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (loading) "Mise à jour..." else "Enregistrer")
                }
            }
            item {
                Button(
                    onClick = {
                        // Navigation vers l'écran de création de chapitre
                            onNavigateToCreateChapter(mangaId)
                    },
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, // Ajouter import si nécessaire
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ajouter un chapitre")
                }
            }

            // BOUTON SUPPRIMER
            item {
                Button(
                    onClick = { vm.delete(mangaId) },
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}
