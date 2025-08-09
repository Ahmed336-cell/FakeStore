package com.elm.fakestore.ui.screens.navigationBar

sealed class Screens (val route : String){
    object Home : Screens("home")
    object Categories : Screens("categories")
    object Cart : Screens("cart")
    object Details : Screens("details/{productId}") {
        fun createRoute(productId: Int) = "details/$productId"
    }
    object CategoryProducts : Screens("categoryProducts/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: Int, categoryName: String) = "categoryProducts/$categoryId/$categoryName"
    }
}