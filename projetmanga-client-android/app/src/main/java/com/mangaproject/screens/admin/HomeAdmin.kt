package com.mangaproject.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.data.repository.UserRepository
import com.mangaproject.ui.tabs.AdminTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdmin(
    logout: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    val token by prefs.token.collectAsState(initial = "")

    if (token.isBlank()) {
        Text("Chargement...", modifier = Modifier.padding(16.dp))
        return
    }

    val api = remember(token) { RetrofitInstance.authedApiService(token) }

    val userRepo = remember(api) { UserRepository(api) }
    val mangaRepo = remember(api) { MangaRepository(api) }
    val storeRepo = remember(api) { StoreRepository(api) }

    val vm: AdminHomeViewModel = viewModel(
        factory = AdminHomeViewModelFactory(userRepo, mangaRepo, storeRepo)
    )

    val tabs = listOf(
        AdminTab.Users,
        AdminTab.Mangas,
        AdminTab.Stores
    )

    var selectedTab by remember { mutableIntStateOf(0) }
    val error by vm.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Espace Admin") },
                actions = {
                    TextButton(onClick = logout) {
                        Text("Déconnexion")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { padding ->

        val modifier = Modifier
            .fillMaxSize()
            .padding(padding)

        Column(modifier = modifier) {

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(Modifier.height(4.dp))
            }

            when (tabs[selectedTab]) {
                AdminTab.Users  -> ScreenAdminUsers(vm, Modifier.weight(1f))
                AdminTab.Mangas -> ScreenAdminMangas(vm, Modifier.weight(1f))
                AdminTab.Stores -> ScreenAdminStores(vm, Modifier.weight(1f))
            }
        }
    }
}
