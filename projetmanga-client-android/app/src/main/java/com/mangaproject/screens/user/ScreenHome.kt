package com.mangaproject.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.model.JikanGenre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHome(vm: HomeViewModel, modifier: Modifier = Modifier) {

    val searchResults by vm.searchResults.collectAsState()
    val genres by vm.genres.collectAsState()
    val loading by vm.searchLoading.collectAsState()

    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf<JikanGenre?>(null) }

    Column(modifier = modifier.padding(16.dp)) {

        // MESSAGE DE BIENVENUE
        Text("Bienvenue 👋", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(12.dp))

        // SEARCH BAR
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                vm.searchByTitle(it)
            },
            label = { Text("Rechercher un manga") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // GENRE DROPDOWN
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedGenre?.name ?: "Filtrer par genre",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        text = { Text(genre.name) },
                        onClick = {
                            selectedGenre = genre
                            expanded = false
                            vm.searchByGenre(genre.mal_id)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Loading
        if (loading) {
            CircularProgressIndicator()
            return
        }

        // LISTE DES RÉSULTATS
        LazyColumn {
            items(searchResults) { manga ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = manga.images?.jpg?.image_url,
                            contentDescription = manga.title,
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(manga.title, style = MaterialTheme.typography.titleMedium)
                            manga.score?.let { Text("Score : $it") }
                        }
                        
                        // Icône étoile pour les favoris (mangas Jikan)
                        IconButton(
                            onClick = {
                                val mangaId = manga.mal_id?.toString() ?: return@IconButton
                                val isFavorite = vm.isFavorite(mangaId)
                                if (isFavorite) {
                                    vm.removeFavorite(mangaId)
                                } else {
                                    vm.addFavorite(mangaId, "jikan")
                                }
                            }
                        ) {
                            val mangaId = manga.mal_id?.toString() ?: ""
                            val isFavorite = vm.isFavorite(mangaId)
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
