import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.mangaproject.screens.adminmanga.CreateMangaViewModel
import androidx.compose.ui.Modifier



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCreateManga(
    vm: CreateMangaViewModel,
    modifier: Modifier = Modifier
) {
    val genres by vm.genres.collectAsState()
    val loading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    var nom by remember { mutableStateOf("") }
    var auteur by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    // Sélection des genres
    var selectedGenres by remember { mutableStateOf<List<String>>(emptyList()) }
    var genreMenuOpened by remember { mutableStateOf(false) }

    fun resetFields() {
        nom = ""
        auteur = ""
        description = ""
        imageUrl = ""
        date = ""
        selectedGenres = emptyList()
    }

    if (success) {
        AlertDialog(
            onDismissRequest = { vm.resetState() },
            title = { Text("Succès") },
            text = { Text("Manga créé avec succès") },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetState()
                    resetFields()
                }) {
                    Text("OK")
                }

            }
        )
    }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom du manga") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = auteur,
                onValueChange = { auteur = it },
                label = { Text("Auteur") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date de sortie (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL image") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ExposedDropdownMenuBox(
                expanded = genreMenuOpened,
                onExpandedChange = { genreMenuOpened = !genreMenuOpened }
            ) {
                OutlinedTextField(
                    value = selectedGenres
                        .mapNotNull { id -> genres.firstOrNull { it.id == id }?.name }
                        .joinToString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Genres") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genreMenuOpened) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = genreMenuOpened,
                    onDismissRequest = { genreMenuOpened = false }
                ) {
                    genres.forEach { genre ->
                        DropdownMenuItem(
                            text = { Text(genre.name) },
                            onClick = {
                                selectedGenres = if (selectedGenres.contains(genre.id))
                                    selectedGenres - genre.id
                                else
                                    selectedGenres + genre.id
                            }
                        )
                    }
                }
            }

        }

        item {
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    vm.createManga(
                        nom = nom,
                        description = description,
                        dateDeSortie = date,
                        urlImage = imageUrl,
                        auteur = auteur,
                        genresIds = selectedGenres
                    )

                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Création..." else "Créer le manga")
            }
        }


    }

}
