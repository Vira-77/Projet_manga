package com.mangaproject.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ScreenMagasins(
    vm: HomeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val stores by vm.stores.collectAsState()

    Column(modifier = modifier.padding(12.dp)) {

        Text(
            text = "Magasins partenaires (${stores.size})",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {

            items(stores) { store ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        // NOM
                        Text(
                            text = store.nom,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // ADRESSE
                        store.adresse?.let {
                            Text("📍 $it", style = MaterialTheme.typography.bodyMedium)
                        }

                        // TELEPHONE
                        store.telephone?.let {
                            Text("📞 $it", style = MaterialTheme.typography.bodyMedium)
                        }

                        // EMAIL
                        store.email?.let {
                            Text("✉️ $it", style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // BOUTON MAP
                        Button(
                            onClick = {
                                navController.navigate("map_stores")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Voir sur la carte")
                        }
                    }
                }
            }
        }
    }
}
