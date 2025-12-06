package com.mangaproject.screens.user

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.ReadingHistoryRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.ui.tabs.UserTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUser(
    navController: NavHostController,
    prefs: UserPreferences,
    logout: () -> Unit = {}
) {

    val api = remember { RetrofitInstance.apiService }
    val token by prefs.token.collectAsState(initial = "")
    
    val mangaRepo = remember { MangaRepository(api) }
    val storeRepo = remember { StoreRepository(api) }
    val readingHistoryRepo = remember(token) {
        if (token.isNotBlank()) {
            ReadingHistoryRepository(RetrofitInstance.authedApiService(token))
        } else null
    }

    val vm: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = HomeViewModelFactory(mangaRepo, storeRepo, prefs, readingHistoryRepo)
    )

    val tabs = listOf(
        UserTab.Home,
        UserTab.Favorites,
        UserTab.Tendances,
        UserTab.Communautes,
        UserTab.Magasins,
        UserTab.History
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Espace utilisateur") },
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

        val modifier = Modifier.fillMaxSize().padding(padding)

        when (tabs[selectedTab]) {
            UserTab.Home -> ScreenHome(vm, modifier)
            UserTab.Favorites -> ScreenFavorites(vm, modifier)
            UserTab.Tendances -> ScreenTendances(vm, modifier, onOpen = { id ->
                navController.navigate("manga_detail/$id")
            })
            UserTab.Communautes -> ScreenCommunautes(vm, modifier)
            UserTab.Magasins -> ScreenMagasins(vm, modifier)
            UserTab.History -> ScreenHistory(vm, modifier, onOpen = { id ->
                navController.navigate("manga_detail/$id")
            })
        }
    }
}

