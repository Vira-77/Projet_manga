package com.mangaproject.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mangaproject.ui.login.LoginScreen
import com.mangaproject.ui.login.LoginViewModel
import com.mangaproject.ui.login.LoginViewModelFactory
import com.mangaproject.ui.register.RegisterScreen
import com.mangaproject.ui.register.RegisterViewModel
import com.mangaproject.ui.register.RegisterViewModelFactory
import com.mangaproject.data.datastore.UserPreferences
import com.mangaproject.screens.admin.HomeAdmin
import com.mangaproject.screens.adminmanga.HomeAdminManga
import com.mangaproject.screens.user.HomeUser
import com.mangaproject.ui.logout.LogoutViewModel
import com.mangaproject.ui.logout.LogoutViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.mangaproject.data.api.RetrofitInstance
import com.mangaproject.data.repository.GenreRepository
import com.mangaproject.data.repository.MangaRepository
import com.mangaproject.data.repository.StoreRepository
import com.mangaproject.screens.adminmanga.EditMangaScreen
import com.mangaproject.screens.adminmanga.EditMangaViewModel
import com.mangaproject.screens.adminmanga.EditMangaViewModelFactory
import com.mangaproject.screens.manga.MangaDetailScreen
import androidx.compose.material3.*




@Composable
fun AppNav(navController: NavHostController) {

    val context = LocalContext.current
    val prefs = UserPreferences(context)

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.LOGIN
    ) {

        // LOGIN
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

        // REGISTER
        composable(NavigationRoutes.REGISTER) {

            val vm: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory()
            )

            RegisterScreen(
                viewModel = vm,
                onSuccess = {
                    navController.navigate(NavigationRoutes.LOGIN) {
                        popUpTo(NavigationRoutes.REGISTER) { inclusive = true }
                    }
                },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        // HOME
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
                "utilisateur" -> HomeUser(navController, prefs = prefs, logout = doLogout)
                "admin_manga" -> HomeAdminManga(navController, prefs = prefs, logout = doLogout)
                "admin" -> HomeAdmin(logout = doLogout)
            }
        }

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

        composable(
            route = "edit_manga/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val mangaId = backStackEntry.arguments?.getString("id") ?: ""

            val prefs = UserPreferences(LocalContext.current)
            val tokenState = prefs.token.collectAsState(initial = "")
            val token = tokenState.value

            // Tant qu'on n'a pas le token, on affiche un loader
            if (token.isBlank()) {
                Text("Chargement de la session...")
                return@composable
            }

            val api = remember(token) {
                RetrofitInstance.authedApiService(token)
            }

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





    }
}
