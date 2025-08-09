package com.elm.fakestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.elm.fakestore.ui.screens.Cart.CartScreen
import com.elm.fakestore.ui.screens.Categories.CateogriesScreen
import com.elm.fakestore.ui.screens.Categories.CategoryProductsScreen
import com.elm.fakestore.ui.screens.Home.HomeScreen
import com.elm.fakestore.ui.screens.deitals.DetailsUi
import com.elm.fakestore.ui.navigationBar.BottomNavigationBar
import com.elm.fakestore.ui.navigationBar.Screens


import com.elm.fakestore.ui.viewModel.CartViewModel
import com.elm.fakestore.ui.viewModel.CategoryViewModel
import com.elm.fakestore.ui.viewModel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

                            MainScreen()

                    }

            }
        }




@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val productViewModel: HomeViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    
    val cartViewModel: CartViewModel = viewModel()
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        val graph =
            navController.createGraph(startDestination = Screens.Home.route) {
                composable(route = Screens.Cart.route) {
                    CartScreen(cartViewModel)
                }

                composable(route = Screens.Home.route) {
                    HomeScreen(navController = navController, viewModel = productViewModel , cartViewModel = cartViewModel)
                }
                composable(route = Screens.Details.route) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                    DetailsUi(productId, cartViewModel)
                }
                composable(route = Screens.Categories.route) {
                    CateogriesScreen(viewModel = categoryViewModel, navController = navController)
                }
                composable(route = Screens.CategoryProducts.route) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
                    val categoryName = backStackEntry.arguments?.getString("categoryName")
                    if (categoryId != null && categoryName != null) {
                        CategoryProductsScreen(
                            categoryId = categoryId,
                            categoryName = categoryName,
                            viewModel = categoryViewModel,
                            navController = navController
                        )
                    }
                }
                

            }
        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding)
        )

    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = {
                Text(
                    text = "Welcome to Fake Store",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                )
            }
        )
    }
