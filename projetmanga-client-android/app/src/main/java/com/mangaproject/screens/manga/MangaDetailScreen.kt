package com.mangaproject.screens.manga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.api.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaDetailScreen(
    id: String,
    onBack: () -> Unit
) {
    var manga by remember { mutableStateOf<com.mangaproject.data.model.JikanManga?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.apiService.getMangaById(id)
            manga = response.manga
        } catch (e: Exception) {
            error = e.message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(manga?.title ?: "Détails du manga") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->

        if (error != null) {
            Text(
                "Erreur : $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(padding).padding(16.dp)
            )
            return@Scaffold
        }

        manga?.let { m ->

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    AsyncImage(
                        model = m.images?.jpg?.image_url,
                        contentDescription = m.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    )
                }

                item {
                    Text(
                        text = m.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                item {
                    m.score?.let {
                        Text("⭐ Score : $it", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                item {
                    m.synopsis?.let {
                        Text("Résumé :", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }

            }
        }
    }
}
