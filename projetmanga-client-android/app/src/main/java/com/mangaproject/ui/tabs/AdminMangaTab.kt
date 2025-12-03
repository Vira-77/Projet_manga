package com.mangaproject.ui.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home

import androidx.compose.ui.graphics.vector.ImageVector

sealed class AdminMangaTab(
    val title: String,
    val icon: ImageVector
) {
    object Home : AdminMangaTab("Accueil", Icons.Filled.Home)
    object Favorites : AdminMangaTab("Favoris", Icons.Filled.Favorite)
    object Tendances : AdminMangaTab("Tendances", Icons.Filled.Search)
    object Communautes : AdminMangaTab("Communauté", Icons.Filled.List)
    object Magasins : AdminMangaTab("Magasins", Icons.Filled.ShoppingCart)
    object CreateManga : AdminMangaTab("Créer manga", Icons.Filled.Add)
    object MyMangas : AdminMangaTab("Mes mangas", Icons.Filled.Edit)
}
