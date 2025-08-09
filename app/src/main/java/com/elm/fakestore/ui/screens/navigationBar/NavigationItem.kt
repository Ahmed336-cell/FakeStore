package com.elm.fakestore.ui.screens.navigationBar

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val isSelected: Boolean = false
)
