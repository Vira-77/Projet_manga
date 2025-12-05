package com.mangaproject.screens.user


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import android.net.Uri
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch

@Composable
fun ScreenProfile(vm: HomeViewModel, onEditProfileClicked: () -> Unit, modifier: Modifier = Modifier) {

    val user by vm.user.collectAsState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    // L'édition est toujours "active" pour les champs
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val selectedImageUri by vm.selectedProfilePictureUri.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            vm.updateSelectedProfilePictureUri(it)
        }
    }

    if (user == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val u = user!!

    // États des champs basés sur l'utilisateur actuel
    // Ces variables stockent les valeurs modifiées
    var editedName by remember(u) { mutableStateOf(u.name) }
    var editedAddress by remember(u) { mutableStateOf(u.address ?: "") }
    var editedBio by remember(u) { mutableStateOf(u.bio ?: "") }

    // --- Fonction de Sauvegarde ---
    val onSaveClicked: () -> Unit = {
        isSaving = true
        errorMessage = null

        // Conversion des chaînes vides en null pour l'API
        val newName = editedName.ifEmpty { null }
        val newAddress = editedAddress.ifEmpty { null }
        val newBio = editedBio.ifEmpty { null }

        scope.launch {
            try {
                vm.updateUser(
                    id = u.id,
                    name = newName,
                    address = newAddress,
                    bio = newBio,
                    profilePicture = null
                )
            } catch (e: Exception) {
                errorMessage = "Erreur lors de la mise à jour : ${e.message}"
            } finally {
                isSaving = false
            }
        }
    }
    // ----------------------------

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Mon Profil",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Logique de la photo de profil
        val uriToDisplay = selectedImageUri ?: if (!u.profilePicture.isNullOrEmpty()) Uri.parse(u.profilePicture) else null
        Box(
            modifier = Modifier.size(120.dp)
        ) {
            if (uriToDisplay != null) {
                Image(
                    painter = rememberAsyncImagePainter(uriToDisplay),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = u.name.take(1).uppercase(),
                        fontSize = 40.sp,
                        color = Color.White
                    )
                }
            }
            IconButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Modifier la photo de profil",
                    tint = Color.White
                )
            }
        }



        Spacer(modifier = Modifier.height(32.dp))

        // Rôle (Affichage simple, non modifiable)
        Text(text = "Rôle: ${u.role}", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(8.dp))

        // Nom (OutlinedTextField)
        OutlinedTextField(
            value = editedName,
            onValueChange = { editedName = it },
            label = { Text("Nom complet") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email (Affichage simple, non modifiable)
        // NOTE: Si l'email doit être modifiable, il doit aussi être un OutlinedTextField
        OutlinedTextField(
            value = u.email,
            onValueChange = { /* Email is read-only here */ },
            label = { Text("Email (Non modifiable)") },
            singleLine = true,
            readOnly = true, // L'email est généralement en lecture seule
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Adresse (OutlinedTextField)
        OutlinedTextField(
            value = editedAddress,
            onValueChange = { editedAddress = it },
            label = { Text("Adresse") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Biographie (OutlinedTextField)
        OutlinedTextField(
            value = editedBio,
            onValueChange = { editedBio = it },
            label = { Text("Biographie") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Message d'erreur
        errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Bouton "Sauvegarder le Profil"
        Button(
            onClick = onSaveClicked,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Sauvegarder le Profil", fontSize = 18.sp)
            }
        }
    }
}