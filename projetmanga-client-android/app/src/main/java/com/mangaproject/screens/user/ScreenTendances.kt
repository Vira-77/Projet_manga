package com.mangaproject.screens.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ScreenTendances(
    vm: HomeViewModel,
    modifier: Modifier = Modifier,
    onOpen: (String) -> Unit
) {
    val trends by vm.trends.collectAsState()

    LazyColumn(
        modifier = modifier.padding(8.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {

        item {
            Text(
                text = "Top ${trends.size} tendances",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        items(trends) { manga ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 6.dp)
                    .clickable {
                        onOpen(manga.mal_id.toString())},
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp)) {

                    AsyncImage(
                        model = manga.images?.jpg?.image_url,
                        contentDescription = manga.title,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = manga.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        manga.score?.let {
                            Text(
                                text = "Score : $it",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
