package com.mangaproject.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangaproject.data.model.Store

@Composable
fun ScreenAdminStores(
    vm: AdminHomeViewModel,
    modifier: Modifier = Modifier
) {
    val stores by vm.stores.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {

        Button(
            onClick = { vm.refreshStores() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔄 Rafraîchir les magasins")
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stores) { store: Store ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(store.nom, style = MaterialTheme.typography.titleMedium)
                        Text(store.adresse ?: "", style = MaterialTheme.typography.bodyMedium)

                        Spacer(Modifier.height(8.dp))

                        TextButton(
                            onClick = { vm.deleteStore(store.id) }
                        ) {
                            Text("Supprimer", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
