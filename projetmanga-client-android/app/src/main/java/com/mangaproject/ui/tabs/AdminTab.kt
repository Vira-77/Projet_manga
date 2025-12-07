package com.mangaproject.ui.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AdminTab(val title: String, val icon: ImageVector) {

    object Users : AdminTab("Utilisateurs", Icons.Filled.Person)
    object Mangas : AdminTab("Mangas", Icons.Filled.Edit)
    object Stores : AdminTab("Magasins", Icons.Filled.ShoppingCart)
}
