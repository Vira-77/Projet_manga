package com.mangaproject.screens.user

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@Composable
fun ScreenProfile(vm: HomeViewModel, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val user by vm.user.collectAsState()
    val uploadState by vm.uploadState.collectAsState()
    val selectedImageUri by vm.selectedProfilePictureUri.collectAsState()
    val scope = rememberCoroutineScope()

    var isSaving by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d("ScreenProfile", "📸 Image sélectionnée: $it")
            vm.updateSelectedProfilePictureUri(it)
        }
    }

    // ✅ Gérer les états d'upload de manière sécurisée
    LaunchedEffect(uploadState) {
        when (val state = uploadState) {
            is UploadState.Success -> {
                Log.d("ScreenProfile", "✅ Upload réussi: ${state.message}")
                isSaving = false
                showSuccessMessage = true
                errorMessage = null

                kotlinx.coroutines.delay(3000)
                showSuccessMessage = false
                vm.resetUploadState()
            }
            is UploadState.Error -> {
                Log.e("ScreenProfile", "❌ Erreur upload: ${state.message}")
                isSaving = false
                errorMessage = state.message
                showSuccessMessage = false
                vm.resetUploadState()
            }
            is UploadState.Loading -> {
                Log.d("ScreenProfile", "⏳ Upload en cours...")
                isSaving = true
            }
            UploadState.Idle -> {
                // Ne rien faire
            }
        }
    }

    if (user == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text("Chargement du profil...", color = Color.Gray)
            }
        }
        return
    }

    val u = user!!

    var editedName by remember(u) { mutableStateOf(u.name) }
    var editedAddress by remember(u) { mutableStateOf(u.address ?: "") }
    var editedBio by remember(u) { mutableStateOf(u.bio ?: "") }

    // ✅ Fonction de sauvegarde simplifiée
    val onSaveClicked: () -> Unit = {
        vm.saveProfile(
            context = context,
            userId = u.id,
            name = editedName.ifBlank { null },
            address = editedAddress.ifBlank { null },
            bio = editedBio.ifBlank { null },
            hasImageToUpload = selectedImageUri != null
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Mon Profil",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .padding(8.dp)
        ) {
            // ✅ Utiliser l'image du user (rechargée après upload)
            val imageToDisplay = when {
                // Afficher la preview locale uniquement si pas encore uploadée
                selectedImageUri != null && uploadState !is UploadState.Success -> {
                    Log.d("ScreenProfile", "🖼️ Affichage preview locale")
                    selectedImageUri
                }
                !u.profilePicture.isNullOrEmpty() -> {
                    val url = "http://10.0.2.2:3000${u.profilePicture}"
                    Log.d("ScreenProfile", "🌐 Affichage image serveur: $url")
                    Uri.parse(url)
                }
                else -> null
            }

            if (imageToDisplay != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageToDisplay),
                    contentDescription = "Photo de profil",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            )
                        )
                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = u.name.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 48.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (uploadState is UploadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            FloatingActionButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(48.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifier la photo",
                    modifier = Modifier.size(24.dp)
                )
            }

            if (!u.profilePicture.isNullOrEmpty() || selectedImageUri != null) {
                FloatingActionButton(
                    onClick = {
                        Log.d("ScreenProfile", "🗑️ Suppression demandée")
                        scope.launch {
                            if (selectedImageUri != null) {
                                vm.updateSelectedProfilePictureUri(null)
                                errorMessage = "Sélection annulée"
                            } else {
                                vm.deleteProfilePicture()
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer la photo",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = "Rôle: ${u.role?.uppercase() ?: "INCONNU"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showSuccessMessage) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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
                        text = "Profil mis à jour avec succès !",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        errorMessage?.let { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nom complet") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = u.email,
                    onValueChange = {},
                    label = { Text("Email") },
                    singleLine = true,
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                OutlinedTextField(
                    value = editedAddress,
                    onValueChange = { editedAddress = it },
                    label = { Text("Adresse") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedBio,
                    onValueChange = { editedBio = it },
                    label = { Text("Biographie") },
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSaveClicked,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            if (isSaving) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Sauvegarde en cours...", fontSize = 18.sp)
                }
            } else {
                Text(
                    text = "💾 Sauvegarder le Profil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}