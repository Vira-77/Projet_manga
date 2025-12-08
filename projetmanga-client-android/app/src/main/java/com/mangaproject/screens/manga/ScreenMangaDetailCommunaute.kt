package com.mangaproject.screens.manga

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.model.Manga
import com.mangaproject.data.model.Chapter
import com.mangaproject.data.model.Genre
import com.mangaproject.utils.ImageUtils.toFullImageUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenMangaDetailCommunaute(
    id: String,
    onBack: () -> Unit,
    onChapterClick: ((String) -> Unit)? = null
) {
    var manga by remember { mutableStateOf<Manga?>(null) }
    var chapters by remember { mutableStateOf<List<Chapter>>(emptyList()) }
    var genres by remember { mutableStateOf<List<Genre>>(emptyList()) }
    var selectedGenreNames by remember { mutableStateOf<List<String>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var chaptersLoading by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        try {
            // Charger le manga
            val mangaResponse = RetrofitInstance.apiService.getMangaLocalById(id)
            manga = mangaResponse.manga

            if (manga == null) {
                error = "Manga introuvable"
                return@LaunchedEffect
            }

            // Charger tous les genres disponibles
            try {
                genres = RetrofitInstance.apiService.getAllGenres().genres
            } catch (e: Exception) {
                // Si erreur, continuer sans les genres
            }

            // Mapper les IDs de genres du manga vers leurs noms
            manga?.genres?.let { genreIds ->
                selectedGenreNames = genreIds.mapNotNull { id ->
                    genres.firstOrNull { it.id == id }?.name
                }
            }

            // Charger les chapitres
            chaptersLoading = true
            try {
                val chaptersResponse = RetrofitInstance.apiService.getAllChapterById(id)

                if (chaptersResponse.isSuccessful) {
                    val body = chaptersResponse.body()
                    if (body != null) {
                        chapters = body.chapters
                    } else {
                        chapters = emptyList()
                    }
                } else {
                    chapters = emptyList()
                }
            } catch (e: Exception) {
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
                        model = m.urlImage.toFullImageUrl(),
                        contentDescription = m.nom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }

                // Nom du manga
                item {
                    Text(
                        text = m.nom,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Auteur
                item {
                    Text(
                        text = "✍️ Auteur : ${m.auteur}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Date de sortie
                item {
                    m.dateDeSortie?.let { dateString ->
                        Text(
                            text = "🗓️ Date de sortie : ${dateString.take(10)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Genres
                if (selectedGenreNames.isNotEmpty()) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🏷️ Genres :",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(selectedGenreNames) { genreName ->
                                    SuggestionChip(
                                        onClick = { },
                                        label = { Text(genreName) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Description/Synopsis
                m.description?.let {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "📖 Résumé :",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Divider avant les chapitres
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // SECTION CHAPITRES
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

                // LISTE DES CHAPITRES
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
                    items(chapters) { chapter ->
                        ChapterCard(
                            chapter = chapter,
                            onClick = {
                                chapter.id?.let { chapterId ->
                                    onChapterClick?.invoke(chapterId)
                                }
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

@Composable
fun ChapterCard(
    chapter: Chapter,
    onClick: () -> Unit
) {
    val hasValidId = !chapter.id.isNullOrBlank()
    val hasPages = !chapter.pages.isNullOrEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = hasValidId, onClick = onClick),
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
                    text = chapter.titre ?: "Sans titre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (hasPages) {
                        Text(
                            text = "${chapter.pages!!.size} pages",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    chapter.chapterNumber?.let { number ->
                        Text(
                            text = "Ch. $number",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Lire le chapitre",
                tint = if (hasValidId && hasPages)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}