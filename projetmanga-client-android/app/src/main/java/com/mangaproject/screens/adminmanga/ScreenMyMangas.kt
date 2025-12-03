package com.mangaproject.screens.adminmanga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.model.Manga

@Composable
fun ScreenMyMangas(
    vm: AdminMangaListViewModel,
    modifier: Modifier = Modifier,
    onEdit: (String) -> Unit = {}
) {
    val mangas by vm.mangas.collectAsState()
    val error by vm.error.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        Button(
            onClick = { vm.loadMangas() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔄 Rafraîchir la liste")
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mangas) { manga ->
                MangaCard(
                    manga = manga,
                    onDelete = { vm.deleteManga(manga.id) },
                    onEdit = { onEdit(manga.id) }
                )
            }
        }
    }
}

@Composable
fun MangaCard(
    manga: Manga,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AsyncImage(
                model = manga.urlImage,
                contentDescription = manga.nom,
                modifier = Modifier.size(80.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(manga.nom, style = MaterialTheme.typography.titleMedium)
                Text("Auteur : ${manga.auteur}", style = MaterialTheme.typography.bodyMedium)

                if (!manga.description.isNullOrBlank()) {
                    Text(
                        manga.description!!,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    OutlinedButton(onClick = onEdit) {
                        Text("Modifier")
                    }
                }
            }
        }
    }
}
