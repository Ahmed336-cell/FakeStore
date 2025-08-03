package com.elm.fakestore.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.elm.fakestore.data.Products
import com.elm.fakestore.network.RetrofitClient
import com.elm.fakestore.screens.deitals.DetailsUi
import com.elm.fakestore.ui.theme.FakeStoreTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FakeStoreTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                        ) {
                            NavGraph()
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun productList(navController: NavHostController) {
    var products by remember { mutableStateOf<List<Products>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        try{
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiSerice.getProducts()
            }
            products=response
        }catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }

    }
    when{
        isLoading -> Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        error != null -> Text(text = "Error: $error", modifier = Modifier.padding(16.dp))
        else ->  LazyColumn {
            items(products) { product ->
                productCard(   product){
                    navController.navigate("productDetail/${product.id}")

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
                Text(text = product.title ,modifier = Modifier.padding(bottom = 8.dp) , fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = product.price.toString() + " EGP", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "productList") {
        composable("productList") {
            productList(navController)
        }
        composable("productDetail/{id}") { backStackEntry ->
            val productJson = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            id?.let {
                DetailsUi(productJson)
            }



        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FakeStoreTheme {
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
}