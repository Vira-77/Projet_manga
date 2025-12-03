package com.mangaproject.screens.admin

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
fun ScreenAdminMangas(
    vm: AdminHomeViewModel,
    modifier: Modifier = Modifier
) {
    val mangas by vm.mangas.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {

        Button(
            onClick = { vm.refreshMangas() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔄 Rafraîchir les mangas")
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mangas) { manga: Manga ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(Modifier.padding(12.dp)) {

                        AsyncImage(
                            model = manga.urlImage,
                            contentDescription = manga.nom,
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f)) {
                            Text(manga.nom, style = MaterialTheme.typography.titleMedium)
                            Text("Auteur : ${manga.auteur}")
                        }

                        TextButton(
                            onClick = { vm.deleteManga(manga.id) }
                        ) {
                            Text("Supprimer", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
