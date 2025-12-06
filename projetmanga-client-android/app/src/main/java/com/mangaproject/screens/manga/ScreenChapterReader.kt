package com.mangaproject.screens.manga

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.model.Chapter
import com.mangaproject.data.model.Page

// ✅ Enum pour les modes de lecture
enum class ReadingMode {
    VERTICAL,   // Scroll vertical
    HORIZONTAL  // Swipe horizontal
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenChapterReader(
    chapterId: String,
    onBack: () -> Unit
) {
    var chapter by remember { mutableStateOf<Chapter?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // ✅ État pour le mode de lecture
    var readingMode by remember { mutableStateOf(ReadingMode.VERTICAL) }

    // Charger le chapitre avec ses pages
    LaunchedEffect(chapterId) {
        try {
            isLoading = true
            println("Chargement du chapitre: $chapterId")

            val response = RetrofitInstance.apiService.getChapterById(
                id = chapterId
            )

            if (response.isSuccessful) {
                chapter = response.body()?.chapter
                println("✅ Chapitre chargé: ${chapter?.titre}")
                println("📄 ${chapter?.pages?.size} pages trouvées")
            } else {
                error = "Erreur ${response.code()}: ${response.message()}"
                println("❌ Erreur API: ${response.code()}")
            }

        } catch (e: Exception) {
            error = "Erreur de connexion: ${e.message}"
            println("💥 Exception: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = chapter?.titre ?: "Lecture",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    // ✅ Bouton pour changer le mode de lecture
                    IconButton(
                        onClick = {
                            readingMode = when (readingMode) {
                                ReadingMode.VERTICAL -> ReadingMode.HORIZONTAL
                                ReadingMode.HORIZONTAL -> ReadingMode.VERTICAL
                            }
                        }
                    ) {
                        Icon(
                            imageVector = when (readingMode) {
                                ReadingMode.VERTICAL -> Icons.Default.SwapHoriz
                                ReadingMode.HORIZONTAL -> Icons.Default.SwapVert
                            },
                            contentDescription = "Changer le mode de lecture",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->

        when {
            // État de chargement
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Chargement du chapitre...")
                    }
                }
            }

            // État d'erreur
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = error ?: "Erreur inconnue",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = onBack) {
                            Text("Retour")
                        }
                    }
                }
            }

            // Afficher le chapitre
            chapter != null -> {
                val pages = chapter!!.pages

                if (pages.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Aucune page disponible",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(onClick = onBack) {
                                Text("Retour")
                            }
                        }
                    }
                } else {
                    // ✅ Afficher selon le mode de lecture
                    when (readingMode) {
                        ReadingMode.VERTICAL -> {
                            VerticalReaderMode(
                                chapter = chapter!!,
                                pages = pages,
                                padding = padding,
                                onBack = onBack
                            )
                        }
                        ReadingMode.HORIZONTAL -> {
                            HorizontalReaderMode(
                                chapter = chapter!!,
                                pages = pages,
                                padding = padding,
                                onBack = onBack
                            )
                        }
                    }
                }
            }
        }
    }
}

// ✅ Mode de lecture VERTICAL (scroll)
@Composable
fun VerticalReaderMode(
    chapter: Chapter,
    pages: List<Page>,
    padding: PaddingValues,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Header avec infos du chapitre
        item {
            ChapterHeader(chapter = chapter, totalPages = pages.size)
        }

        // Afficher chaque page
        items(
            items = pages,
            key = { page -> page.numero }
        ) { page ->
            PageImageVertical(
                page = page,
                totalPages = pages.size
            )
        }

        // Footer "Fin du chapitre"

        item {
            ChapterFooter(onBack = onBack)
        }
    }
}

// ✅ Mode de lecture HORIZONTAL (swipe)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalReaderMode(
    chapter: Chapter,
    pages: List<Page>,
    padding: PaddingValues,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // ✅ HorizontalPager pour swipe
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val page = pages[pageIndex]

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = page.urlImage,
                    contentDescription = "Page ${page.numero}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit // ✅ Fit pour mode horizontal
                )
            }
        }

        // ✅ Indicateur de page en overlay
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "${pagerState.currentPage + 1} / ${pages.size}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // ✅ Titre du chapitre en overlay
        /*
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = chapter.titre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Bouton retour si on est à la dernière page
                if (pagerState.currentPage == pages.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onBack) {
                        Text("Retour aux chapitres")
                    }
                }
            }
        }
        */
    }
}

//
@Composable
fun ChapterHeader(chapter: Chapter, totalPages: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = chapter.titre,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$totalPages pages • Mode vertical",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ChapterFooter(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "📖 Fin du chapitre",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(onClick = onBack) {
                    Text("Retour aux chapitres")
                }
            }
        }
    }
}

@Composable
fun PageImageVertical(page: Page, totalPages: Int) {
    Column {
        // Indicateur de numéro de page
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "Page ${page.numero} / $totalPages",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Image de la page
        AsyncImage(
            model = page.urlImage,
            contentDescription = "Page ${page.numero}",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentScale = ContentScale.FillWidth // ✅ FillWidth pour vertical
        )
    }
}