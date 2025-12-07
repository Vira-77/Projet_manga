package com.mangaproject.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.data.repository.GenreRepository
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.screens.adminmanga.EditMangaScreen
import com.mangaproject.screens.adminmanga.EditMangaViewModel
import com.mangaproject.screens.adminmanga.EditMangaViewModelFactory
import com.mangaproject.screens.manga.MangaDetailScreen
import com.mangaproject.ui.login.LoginScreen
import com.mangaproject.ui.login.LoginViewModel
import com.mangaproject.ui.login.LoginViewModelFactory
import com.mangaproject.ui.register.RegisterScreen
import com.mangaproject.ui.register.RegisterViewModel
import com.mangaproject.ui.register.RegisterViewModelFactory
import com.mangaproject.ui.logout.LogoutViewModel
import com.mangaproject.ui.logout.LogoutViewModelFactory
import com.mangaproject.screens.admin.HomeAdmin
import com.mangaproject.screens.adminmanga.HomeAdminManga
import com.mangaproject.screens.user.HomeUser
import com.mangaproject.screens.chat.ChatScreen
import com.mangaproject.screens.map.LocationViewModel
import com.mangaproject.screens.map.StoreMapScreen
import com.mangaproject.screens.user.HomeViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.screens.user.HomeViewModelFactory
import com.mangaproject.screens.manga.ScreenChapterReader
import com.mangaproject.screens.manga.ScreenMangaDetailCommunaute
import com.mangaproject.data.repository.UserRepository


@Composable
fun AppNav(navController: NavHostController) {

    val context = LocalContext.current
    val prefs = UserPreferences(context)

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.LOGIN
    ) {

        // --------------------------
        // LOGIN
        // --------------------------
        composable(NavigationRoutes.LOGIN) {

            val vm: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(prefs)
            )

            LoginScreen(
                viewModel = vm,
                onSuccess = {
                    navController.navigate(NavigationRoutes.HOME) {
                        popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = {
                    navController.navigate(NavigationRoutes.REGISTER)
                }
            )
        }

        // --------------------------
        // REGISTER
        // --------------------------
        composable(NavigationRoutes.REGISTER) {

            val vm: RegisterViewModel = viewModel(factory = RegisterViewModelFactory())

            RegisterScreen(
                viewModel = vm,
                onSuccess = {
                    navController.navigate(NavigationRoutes.LOGIN) {
                        popUpTo(NavigationRoutes.REGISTER) { inclusive = true }
                    }
                },
                onGoToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // --------------------------
        // HOME ROUTING (selon rôle)
        // --------------------------
        composable(NavigationRoutes.HOME) {

            val logoutVM: LogoutViewModel = viewModel(
                factory = LogoutViewModelFactory(prefs)
            )

            val role by prefs.role.collectAsState(initial = "")

            val doLogout = {
                logoutVM.logout {
                    navController.navigate(NavigationRoutes.LOGIN) {
                        popUpTo(NavigationRoutes.HOME) { inclusive = true }
                    }
                }
            }

            when (role) {
                "utilisateur" ->
                    HomeUser(
                        navController = navController,
                        prefs = prefs,
                        logout = doLogout
                    )

                "admin_manga" ->
                    HomeAdminManga(navController, prefs = prefs, logout = doLogout)

                "admin" ->
                    HomeAdmin(logout = doLogout)
            }
        }

        // --------------------------
        // MANGA DETAIL
        // --------------------------
        composable(
            route = NavigationRoutes.MANGA_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val mangaId = backStackEntry.arguments?.getString("id") ?: ""

            MangaDetailScreen(
                id = mangaId,
                onBack = { navController.popBackStack() }
            )
        }


        // pour les mangas ne provenant pas de l'api
        composable(
            route = NavigationRoutes.MANGA_DETAIL_COMMUNAUTE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val mangaId = backStackEntry.arguments?.getString("id") ?: ""
            ScreenMangaDetailCommunaute(
                id = mangaId,
                onBack = { navController.popBackStack() },
                onChapterClick = { chapterId ->
                    navController.navigate("chapterReader/$chapterId")
                }
            )
        }

        // --------------------------
        // EDIT MANGA
        // --------------------------
        composable(
            route = "edit_manga/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val mangaId = backStackEntry.arguments?.getString("id") ?: ""

            val token by prefs.token.collectAsState(initial = "")

            if (token.isBlank()) {
                Text("Chargement…")
                return@composable
            }

            val api = remember(token) { RetrofitInstance.authedApiService(token) }
            val mangaRepo = remember(api) { MangaRepository(api) }
            val genreRepo = remember(api) { GenreRepository(api) }

            val vm: EditMangaViewModel = viewModel(
                factory = EditMangaViewModelFactory(mangaRepo, genreRepo)
            )

            EditMangaScreen(
                mangaId = mangaId,
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }



        composable(
            route = "chapterReader/{chapterId}",
            arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
        ) { backStackEntry ->

            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""

            ScreenChapterReader(
                chapterId = chapterId,
                onBack = { navController.popBackStack() },
                onNavigateToChapter = { newChapterId ->
                    // Naviguer vers le nouveau chapitre
                    navController.navigate("chapterReader/$newChapterId") {
                        popUpTo("chapterReader/$chapterId") { inclusive = true }
                    }
                }
            )
        }

        // --------------------------
        // CHAT IA
        // --------------------------
        composable(NavigationRoutes.CHAT_AI) {
            ChatScreen()
        }

        // --------------------------
        // MAP STORES
        // --------------------------

        composable("map_stores") {

            val context = LocalContext.current
            val prefs = UserPreferences(context)

            val api = remember { RetrofitInstance.apiService }
            val mangaRepo = remember { MangaRepository(api) }
            val storeRepo = remember { StoreRepository(api) }
            val userRepo = remember { UserRepository(api) }

            val homeVm: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(mangaRepo, storeRepo, userRepo,prefs)
            )

            val locationVM: LocationViewModel = viewModel()

            LaunchedEffect(Unit) {
                homeVm.loadStores()
                locationVM.loadLastLocation()
                locationVM.startLocationUpdates()
            }

            StoreMapScreen(
                stores = homeVm.stores.collectAsState().value,
                vm = locationVM,
                onBack = { navController.popBackStack() }
            )
        }


    }
}
