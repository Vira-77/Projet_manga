package com.mangaproject.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.model.Manga

@Composable
fun ScreenFavorites(vm: HomeViewModel, modifier: Modifier = Modifier) {

    val favorites by vm.favorites.collectAsState()

    Column(modifier = modifier.padding(8.dp)) {

        Text(
            text = "Vos mangas favoris",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (favorites.isEmpty()) {
            Text(
                "Vous n’avez aucun favori pour le moment.",
                style = MaterialTheme.typography.bodyMedium
            )
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(favorites) { manga ->
                FavoriteItem(manga)
            }
        }
    }
}

@Composable
fun FavoriteItem(manga: Manga) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            AsyncImage(
                model = manga.urlImage,
                contentDescription = manga.nom,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = manga.nom,
                    style = MaterialTheme.typography.titleMedium
                )

                manga.auteur.let {
                    Text(
                        text = "Auteur : $it",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
