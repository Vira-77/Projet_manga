package com.mangaproject.screens.adminmanga

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCreateChapter(
    mangaId: String,
    vm: CreateChapterViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val loading by vm.loading.collectAsState()
    val uploadState by vm.uploadState.collectAsState()
    val selectedImages by vm.selectedImages.collectAsState()
    val uploadProgress by vm.uploadProgress.collectAsState()

    var numeroChapter by remember { mutableStateOf("") }
    var titreChapter by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        vm.addImages(uris)
    }

    // ✅ Gérer les états d'upload (comme dans ScreenProfile)
    LaunchedEffect(uploadState) {
        when (val state = uploadState) {
            is UploadState.Success -> {
                showSuccessMessage = true
                errorMessage = null
                delay(2000)
                vm.resetState()
                onBack()
            }
            is UploadState.Error -> {
                errorMessage = state.message
                showSuccessMessage = false
                vm.resetUploadState()
            }
            is UploadState.Loading -> {
                // Loading géré par le state
            }
            UploadState.Idle -> {
                // Ne rien faire
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un chapitre") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // MESSAGES
            if (showSuccessMessage) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "✅", fontSize = 24.sp)
                            Text(
                                text = "Chapitre créé avec succès !",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            errorMessage?.let { msg ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "❌", fontSize = 24.sp)
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // NUMÉRO
            item {
                OutlinedTextField(
                    value = numeroChapter,
                    onValueChange = { numeroChapter = it },
                    label = { Text("Numéro du chapitre") },
                    placeholder = { Text("Ex: 1, 2, 3...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // TITRE
            item {
                OutlinedTextField(
                    value = titreChapter,
                    onValueChange = { titreChapter = it },
                    label = { Text("Titre du chapitre (optionnel)") },
                    placeholder = { Text("Ex: Le début de l'aventure") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // BOUTON SÉLECTIONNER
            item {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sélectionner les pages (${selectedImages.size})")
                }
            }

            // LISTE DES IMAGES
            items(selectedImages) { uri ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Page",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 8.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Page ${selectedImages.indexOf(uri) + 1}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        IconButton(onClick = { vm.removeImage(uri) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Supprimer",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // PROGRESSION
            if (uploadState is UploadState.Loading && selectedImages.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Upload: $uploadProgress / ${selectedImages.size} pages")
                        LinearProgressIndicator(
                            progress = uploadProgress.toFloat() / selectedImages.size.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // BOUTON CRÉER
            item {
                Button(
                    onClick = {
                        vm.createChapter(
                            context = context,
                            mangaId = mangaId,
                            numero = numeroChapter.toIntOrNull() ?: 0,
                            titre = titreChapter.ifBlank { null }
                        )
                    },
                    enabled = !loading && numeroChapter.isNotBlank() && selectedImages.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (loading) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Création en cours...", fontSize = 18.sp)
                        }
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("📖 Créer le chapitre", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}