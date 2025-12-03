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
fun ScreenCommunautes(vm: HomeViewModel, modifier: Modifier = Modifier) {

    val localMangas by vm.localMangas.collectAsState()

    Column(modifier = modifier.padding(8.dp)) {

        Text(
            text = "Mangas de la communauté",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (localMangas.isEmpty()) {
            Text("Aucun manga local disponible.", style = MaterialTheme.typography.bodyMedium)
            return
        } else {
            Button(
                onClick = { vm.refresh() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔄 Rafraîchir la liste")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(localMangas) { manga ->
                CommunautesItem(manga)
            }
        }
    }
}

@Composable
fun CommunautesItem(manga: Manga) {
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

                Text(
                    text = "Auteur : ${manga.auteur}",
                    style = MaterialTheme.typography.bodyMedium
                )

                manga.description?.let {
                    Text(
                        text = it.take(80) + "...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
