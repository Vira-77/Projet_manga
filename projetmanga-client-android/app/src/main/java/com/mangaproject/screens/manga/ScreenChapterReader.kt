package com.mangaproject.screens.manga

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.model.Chapter
import com.mangaproject.data.model.ChapterNavigation
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
    onBack: () -> Unit,
    onNavigateToChapter: (String) -> Unit // ✅ Callback pour navigation
) {
    var chapter by remember { mutableStateOf<Chapter?>(null) }
    var navigation by remember { mutableStateOf<ChapterNavigation?>(null) } // ✅ État navigation
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var readingMode by remember { mutableStateOf(ReadingMode.VERTICAL) }

    // ✅ Charger le chapitre avec navigation
    LaunchedEffect(chapterId) {
        try {
            isLoading = true
            println("🔍 Chargement du chapitre: $chapterId")

            val response = RetrofitInstance.apiService.getChapterById(
                id = chapterId
            )

            if (response.isSuccessful) {
                val body = response.body()
                chapter = body?.chapter
                navigation = body?.navigation // ✅ Récupérer infos navigation

                println("✅ Chapitre: ${chapter?.titre}")
                println("⬅️ Précédent: ${navigation?.previous?.titre}")
                println("➡️ Suivant: ${navigation?.next?.titre}")
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
                    // ✅ Bouton chapitre précédent
                    IconButton(
                        onClick = {
                            navigation?.previous?.id?.let { onNavigateToChapter(it) }
                        },
                        enabled = navigation?.previous != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Chapitre précédent",
                            tint = if (navigation?.previous != null)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }

                    // ✅ Bouton chapitre suivant
                    IconButton(
                        onClick = {
                            navigation?.next?.id?.let { onNavigateToChapter(it) }
                        },
                        enabled = navigation?.next != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Chapitre suivant",
                            tint = if (navigation?.next != null)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }

                    // ✅ Bouton mode lecture
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
                            contentDescription = "Changer le mode de lecture"
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
                                navigation = navigation,
                                onBack = onBack,
                                onNavigateToChapter = onNavigateToChapter
                            )
                        }
                        ReadingMode.HORIZONTAL -> {
                            HorizontalReaderMode(
                                chapter = chapter!!,
                                pages = pages,
                                padding = padding,
                                navigation = navigation,
                                onBack = onBack,
                                onNavigateToChapter = onNavigateToChapter
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
    navigation: ChapterNavigation?,
    onBack: () -> Unit,
    onNavigateToChapter: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
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

        // Footer avec navigation
        item {
            ChapterFooter(
                navigation = navigation,
                onBack = onBack,
                onNavigateToChapter = onNavigateToChapter
            )
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
    navigation: ChapterNavigation?,
    onBack: () -> Unit,
    onNavigateToChapter: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    var isUIVisible by remember { mutableStateOf(true) }

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
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isUIVisible = !isUIVisible }, // ✅ Toggle UI
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = page.urlImage,
                    contentDescription = "Page ${page.numero}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // ✅ Indicateur de page en overlay (apparaît/disparaît au clic)
        if (isUIVisible) {
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
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }


    }
}

// ✅ Composants réutilisables
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
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
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
fun ChapterFooter(
    navigation: ChapterNavigation?,
    onBack: () -> Unit,
    onNavigateToChapter: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📖 Fin du chapitre",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            // ✅ Boutons de navigation
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Chapitre précédent
                navigation?.previous?.let { prev ->
                    OutlinedButton(
                        onClick = { onNavigateToChapter(prev.id) }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Ch. ${prev.chapterNumber}")
                    }
                }

                // Chapitre suivant
                navigation?.next?.let { next ->
                    Button(
                        onClick = { onNavigateToChapter(next.id) }
                    ) {
                        Text("Ch. ${next.chapterNumber}")
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Bouton retour
            TextButton(onClick = onBack) {
                Text("Retour aux chapitres")
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
            contentScale = ContentScale.FillWidth
        )
    }
}