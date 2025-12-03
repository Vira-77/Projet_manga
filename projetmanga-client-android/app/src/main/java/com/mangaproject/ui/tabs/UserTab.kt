package com.mangaproject.ui.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class UserTab(val title: String, val icon: ImageVector) {
    Home("Accueil", Icons.Default.Home),
    Favorites("Favoris", Icons.Default.Favorite),
    Tendances("Tendances", Icons.Default.Search),
    Communautes("Communauté", Icons.Default.Search),
    Magasins("Magasins", Icons.Default.ShoppingCart)
}
