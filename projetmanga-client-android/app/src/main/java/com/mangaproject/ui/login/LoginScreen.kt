package com.mangaproject.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangaproject.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (state.success) {
        onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_manga_corp),
            contentDescription = "Logo MangaCorp",
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))

        // Titre de l'application
        Text(
            text = "MangaCorp",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Votre bibliothèque manga",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(48.dp))

        // Champ Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // Champ Mot de passe
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(24.dp))

        // Bouton de connexion
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !state.isLoading
        ) {
            Text(
                text = "Se connecter",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        // Bouton créer un compte
        TextButton(onClick = onGoToRegister) {
            Text("Créer un compte")
        }

        Spacer(Modifier.height(16.dp))

        // Indicateur de chargement
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        // Message d'erreur
        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}