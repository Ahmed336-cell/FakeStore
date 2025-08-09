package com.elm.fakestore.ui.screens.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.elm.fakestore.data.models.Products
import com.elm.fakestore.data.network.RetrofitClient
import com.elm.fakestore.ui.screens.navigationBar.Screens
import com.elm.fakestore.ui.viewModel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(viewModel : HomeViewModel , navController: NavHostController) {
    val products by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshProducts()
    }

    when{
        isLoading && products.isEmpty() -> Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        errorMessage != null && products.isEmpty() -> Text(text = "Error: $errorMessage", modifier = Modifier.padding(16.dp))
        else -> proudctList(
            products = products, 
            isLoading = isLoading,
            onProductClick = { product ->
                navController.navigate(Screens.Details.createRoute(product.id))
            },
            onLoadMore = {
                viewModel.loadMoreProducts()
            }
        )
    }
}

@Composable
fun proudctList(
    products: List<Products>, 
    isLoading: Boolean,
    onProductClick: (Products) -> Unit,
    onLoadMore: () -> Unit
) {
   if (products.isEmpty()) {
       Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
           Text(text = "No products available", modifier = Modifier.padding(16.dp))
       }
   } else {
       LazyColumn {
           items(products) { product ->
               productCard(product) {
                   onProductClick(product)
               }
           }
           
           // Load More button
           if (products.isNotEmpty()) {
               item {
                   Box(
                       contentAlignment = Alignment.Center,
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(16.dp)
                   ) {
                       if (isLoading) {
                           CircularProgressIndicator()
                       } else {
                           androidx.compose.material3.Button(
                               onClick = onLoadMore
                           ) {
                               Text("Load More Products")
                           }
                       }
                   }
               }
           }
       }
   }
}

@Composable
fun productCard( product: Products,onClick: () -> Unit ) {

    Card (
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick() }

    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            val painter = rememberAsyncImagePainter(model = product.images.firstOrNull())

            Image(
                painter = painter,
                contentDescription = product.title,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp),

                contentScale = ContentScale.Crop


            )
            Spacer(modifier = Modifier.width(16.dp))

                Column(

                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = product.title,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.price.toString() + " EGP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Button(onClick={},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                        .height(40.dp)
                    ) {


                    Text(text = "Add to Cart")
                }

        }
    }
}






