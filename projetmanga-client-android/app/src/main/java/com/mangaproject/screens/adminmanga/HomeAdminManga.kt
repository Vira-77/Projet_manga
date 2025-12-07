package com.mangaproject.screens.adminmanga

import ScreenCreateManga
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.GenreRepository
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.data.repository.UserRepository
import com.mangaproject.screens.user.*
import com.mangaproject.ui.tabs.AdminMangaTab
import com.mangaproject.ui.tabs.UserTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminManga(
    navController: NavHostController,
    prefs: UserPreferences,
    logout: () -> Unit = {}
) {
    // Token & API authentifiée
    val token by prefs.token.collectAsState(initial = "")
    if (token.isBlank()) {
        Text("Chargement...", modifier = Modifier.padding(16.dp))
        return
    }
    val api = remember(token) { RetrofitInstance.authedApiService(token) }

    // Repositories
    val mangaRepo = remember(api) { MangaRepository(api) }
    val genreRepo = remember(api) { GenreRepository(api) }
    val storeRepo = remember(api) { StoreRepository(api) }
    val userRepo = remember(api) { UserRepository(api,token) }

    // ViewModel partagé pour les onglets "user lambda"
    val homeVm: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(mangaRepo, storeRepo,userRepo,prefs)
    )

    // ViewModel pour la liste des mangas (Mes mangas)
    val manageVm: AdminMangaListViewModel = viewModel(
        factory = AdminMangaListViewModelFactory(mangaRepo)
    )

    val tabs = listOf(
        AdminMangaTab.Home,
        AdminMangaTab.Favorites,
        AdminMangaTab.Tendances,
        AdminMangaTab.Communautes,
        AdminMangaTab.Magasins,
        AdminMangaTab.CreateManga,
        AdminMangaTab.MyMangas,
        AdminMangaTab.Profil
    )

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Espace Admin Manga") },
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

            // mêmes écrans que l’utilisateur
            AdminMangaTab.Home ->
                ScreenHome(homeVm, modifier)

            AdminMangaTab.Favorites ->
                ScreenFavorites(
                    homeVm, 
                    modifier,
                    onMangaClick = { mangaId, source ->
                        if (source == "jikan") {
                            navController.navigate("manga_detail/$mangaId")
                        } else {
                            navController.navigate("manga_detail_communaute/$mangaId")
                        }
                    }
                )

            AdminMangaTab.Tendances ->
                ScreenTendances(
                    vm = homeVm,
                    modifier = modifier,
                    onOpen = { id ->
                        navController.navigate("manga_detail/$id")
                    }
                )

            AdminMangaTab.Communautes ->
                ScreenCommunautes(homeVm, modifier,onMangaClick = { mangaId ->
                    navController.navigate("manga_detail_communaute/$mangaId")
                })

            AdminMangaTab.Magasins ->
                ScreenMagasins(homeVm, navController, modifier)

            // Création de manga
            AdminMangaTab.CreateManga -> {
                val createVm: CreateMangaViewModel = viewModel(
                    factory = CreateMangaViewModelFactory(mangaRepo, genreRepo, prefs)
                )
                ScreenCreateManga(
                    vm = createVm,
                    modifier = modifier
                )
            }

            // Gestion de tous les mangas locaux
            AdminMangaTab.MyMangas ->
                ScreenMyMangas(
                    vm = manageVm,
                    modifier = modifier,
                    onEdit = { id ->
                        navController.navigate("edit_manga/$id")
                    }
                )

            AdminMangaTab.Profil -> ScreenProfile(homeVm,modifier)
        }
    }
}
