package com.mangaproject.screens.user

import androidx.compose.foundation.layout.Box
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.ReadingHistoryRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.ui.component.FloatingChatBubble
import com.mangaproject.data.repository.UserRepository
import com.mangaproject.ui.tabs.UserTab


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUser(
    navController: NavHostController,
    prefs: UserPreferences,
    logout: () -> Unit = {}
) {


    val token by prefs.token.collectAsState(initial = "")
    if (token.isBlank()) {
        Text("Chargement...", modifier = Modifier.padding(16.dp))
        return
    }

    val api = remember { RetrofitInstance.authedApiService(token) }
    // Utiliser l'API authentifiée pour les favoris
    val authedApi = remember(token) {
        if (token.isNotBlank()) {
            RetrofitInstance.authedApiService(token)
        } else null
    }


    val mangaRepo = remember(api){
        MangaRepository(authedApi ?: api)
    }
    val storeRepo = remember(api) { StoreRepository(api) }
    val userRepo = remember(api) { UserRepository(api,token) }

    val readingHistoryRepo = remember(token) {
        if (token.isNotBlank()) {
            ReadingHistoryRepository(RetrofitInstance.authedApiService(token))
        } else null
    }

    val vm: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = HomeViewModelFactory(mangaRepo, storeRepo,userRepo, prefs, readingHistoryRepo)
    )


    val tabs = listOf(
        UserTab.Home,
        UserTab.Favorites,
        UserTab.Tendances,
        UserTab.Communautes,
        UserTab.Magasins,
        UserTab.History,
        UserTab.Profil
    )

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {

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

            val modifier = Modifier
                .fillMaxSize()
                .padding(padding)

            when (tabs[selectedTab]) {
                UserTab.Home -> ScreenHome(vm, modifier)
                UserTab.Favorites -> ScreenFavorites(
                    vm, 
                    modifier,
                    onMangaClick = { mangaId, source ->
                        if (source == "jikan") {
                            navController.navigate("manga_detail/$mangaId")
                        } else {
                            navController.navigate("manga_detail_communaute/$mangaId")
                        }
                    }
                )
                UserTab.Tendances -> ScreenTendances(vm, modifier, onOpen = { id ->
                    navController.navigate("manga_detail/$id")
                })

                UserTab.Communautes -> ScreenCommunautes(vm, modifier,onMangaClick = { mangaId ->
                navController.navigate("manga_detail_communaute/$mangaId")
            })
                UserTab.Magasins -> ScreenMagasins(vm, navController, modifier)
            UserTab.Profil -> ScreenProfile(vm,modifier)
                UserTab.History -> {
                    val history by vm.readingHistory.collectAsState()
                    ScreenHistory(vm, modifier, onOpen = { mangaId ->
                        // Trouver l'entrée d'historique pour déterminer la source
                        val historyEntry = history.find { it.mangaId == mangaId }
                        val source = historyEntry?.source ?: "local"
                        if (source == "jikan") {
                            navController.navigate("manga_detail/$mangaId")
                        } else {
                            navController.navigate("manga_detail_communaute/$mangaId")
                        }
                    })
                }
            }
        }
        //LA bulle IA – maintenant au-dessus de tout
        FloatingChatBubble(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            onClick = {
                navController.navigate("chat_screen")
            }
        )
    }
}

