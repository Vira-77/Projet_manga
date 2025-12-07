package com.mangaproject.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

    // ✅ Fond avec dégradé noir/rouge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8B0000), // Rouge foncé en haut
                        Color(0xFF000000), // Noir au milieu
                        Color(0xFF8B0000)  // Rouge foncé en bas
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ Logo
            Image(
                painter = painterResource(id = R.drawable.logo_manga_corp),
                contentDescription = "Logo MangaCorp",
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color(0xFFFF4444), CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(16.dp))

            // ✅ Titre de l'application
            Text(
                text = "MangaCorp",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            )

            Text(
                text = "Votre bibliothèque manga",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFFFF4444), // Rouge vif
                    fontWeight = FontWeight.Light
                )
            )

            Spacer(Modifier.height(48.dp))

            // ✅ Champ Email avec style sombre
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF4444),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFFF4444),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color(0xFFFF4444)
                )
            )

            Spacer(Modifier.height(12.dp))

            // ✅ Champ Mot de passe avec style sombre
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF4444),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFFF4444),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color(0xFFFF4444)
                )
            )

            Spacer(Modifier.height(24.dp))

            // ✅ Bouton de connexion avec dégradé
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF0000),
                                    Color(0xFFCC0000)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Se connecter",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ✅ Bouton créer un compte
            TextButton(onClick = onGoToRegister) {
                Text(
                    text = "Créer un compte",
                    color = Color(0xFFFF6666)
                )
            }

            Spacer(Modifier.height(16.dp))

            // ✅ Indicateur de chargement
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFFFF4444)
                )
            }

            // ✅ Message d'erreur
            state.error?.let {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF4444).copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = it,
                        color = Color(0xFFFF6666),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}