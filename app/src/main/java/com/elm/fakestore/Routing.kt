package com.elm.fakestore

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elm.fakestore.ui.screens.Home.HomeScreen
import com.elm.fakestore.ui.screens.deitals.DetailsUi

class Routing {
    @Composable
    fun NavGraph(){
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "productList") {

            
            composable("productDetail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                DetailsUi(productId)
            }
        }
    }
}