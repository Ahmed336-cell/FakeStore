package com.elm.fakestore.ui.navigationBar

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    val navigationItems = listOf(
        NavigationItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = Screens.Home.route,
        ),

        NavigationItem(
            title = "Cart",
            icon = Icons.Default.ShoppingCart,
            route = Screens.Cart.route
        ),
        NavigationItem(
            title = "Categories",
            icon = Icons.Default.Menu,
            route = Screens.Categories.route
        )
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black,
        tonalElevation = 4.dp,
        modifier = androidx.compose.ui.Modifier.navigationBarsPadding(),
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(
                        item.title,
                        color = if (index == selectedNavigationIndex.intValue)
                            Color.Black
                        else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )

            )
        }
    }
}