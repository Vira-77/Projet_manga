package com.mangaproject.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangaproject.data.model.User

@Composable
fun ScreenAdminUsers(
    vm: AdminHomeViewModel,
    modifier: Modifier = Modifier
) {
    val users by vm.users.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {

        Button(
            onClick = { vm.refreshUsers() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔄 Rafraîchir les utilisateurs")
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user: User ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(user.name, style = MaterialTheme.typography.titleMedium)
                        Text(user.email, style = MaterialTheme.typography.bodyMedium)
                        Text("Rôle actuel : ${user.role}")

                        Spacer(Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(onClick = {
                                vm.changeUserRole(user.id, "utilisateur")
                            }) {
                                Text("Utilisateur")
                            }
                            OutlinedButton(onClick = {
                                vm.changeUserRole(user.id, "admin_manga")
                            }) {
                                Text("Admin manga")
                            }
                            OutlinedButton(onClick = {
                                vm.changeUserRole(user.id, "admin")
                            }) {
                                Text("Admin")
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        TextButton(
                            onClick = { vm.deleteUser(user.id) }
                        ) {
                            Text("❌ Supprimer", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
