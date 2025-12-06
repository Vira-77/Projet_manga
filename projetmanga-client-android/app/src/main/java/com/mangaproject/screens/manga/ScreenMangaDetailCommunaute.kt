package com.mangaproject.screens.manga

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.model.Manga
import com.mangaproject.data.model.Chapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenMangaDetailCommunaute(
    id: String,
    onBack: () -> Unit,
    onChapterClick: ((String) -> Unit)? = null //
) {
    var manga by remember { mutableStateOf<Manga?>(null) }
    var chapters by remember { mutableStateOf<List<Chapter>>(emptyList()) } //
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var chaptersLoading by remember { mutableStateOf(false) } //

    LaunchedEffect(id) {
        try {
            // Charger le manga
            val mangaResponse = RetrofitInstance.apiService.getMangaLocalById(id)
            manga = mangaResponse.manga

            // ✅ Charger les chapitres avec gestion null-safety
            chaptersLoading = true
            try {
                val chaptersResponse = RetrofitInstance.apiService.getAllChapterById(id)

                if (chaptersResponse.isSuccessful) {
                    // ✅ Utiliser l'opérateur sûr (?.)
                    chapters = chaptersResponse.body()?.chapters ?: emptyList()
                } else {
                    println("Erreur API chapitres: ${chaptersResponse.code()}")
                    chapters = emptyList()
                }

            } catch (e: Exception) {
                println("Erreur lors du chargement des chapitres: ${e.message}")
                chapters = emptyList()
            } finally {
                chaptersLoading = false
            }

        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(manga?.nom ?: "Détails du manga") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (error != null) {
            Text(
                "Erreur : $error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            )
            return@Scaffold
        }

        manga?.let { m ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image du manga
                item {
                    AsyncImage(
                        model = m.urlImage,
                        contentDescription = m.nom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(top = 16.dp)
                    )
                }

                // Nom du manga
                item {
                    Text(
                        text = m.nom,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                // Auteur et Date de sortie
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🖋️ Auteur : ${m.auteur}", style = MaterialTheme.typography.bodyLarge)
                        m.dateDeSortie?.let {
                            Text("🗓️ Date de sortie : $it", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                // Genres
                /*
                m.genres?.takeIf { it.isNotEmpty() }?.let { genres ->
                    item {
                        Text("🏷️ Genres :", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(genres.joinToString(separator = ", "), style = MaterialTheme.typography.bodyMedium)
                    }
                }*/

                // Description/Synopsis
                m.description?.let {
                    item {
                        Text("📖 Résumé :", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // ✅ SECTION CHAPITRES
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📚 Chapitres",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        if (chaptersLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "${chapters.size} chapitre(s)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ✅ LISTE DES CHAPITRES
                if (chaptersLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chargement des chapitres...")
                        }
                    }
                } else if (chapters.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "Aucun chapitre disponible pour ce manga",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // ✅ Afficher chaque chapitre
                    items(chapters) { chapter ->
                        ChapterCard(
                            chapter = chapter,
                            onClick = {
                                onChapterClick?.invoke(chapter.id)
                            }
                        )
                    }
                }

                // Espacement final
                item {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

// ✅ Composant pour afficher un chapitre
@Composable
fun ChapterCard(
    chapter: Chapter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = chapter.titre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nombre de pages si disponible
                    chapter.pages?.let { pages ->
                        Text(
                            text = "${pages.size} pages",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Numéro de chapitre si disponible
                    chapter.chapterNumber?.let { number ->
                        Text(
                            text = "Ch. $number",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Icône de lecture
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Lire le chapitre",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}