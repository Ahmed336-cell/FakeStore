package com.elm.fakestore.ui.screens.deitals

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.elm.fakestore.data.models.Products
import com.elm.fakestore.data.network.RetrofitClient
import com.elm.fakestore.ui.viewModel.CartViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.elm.fakestore.R
import com.elm.fakestore.ui.screens.Home.ui.theme.Goldy
import com.elm.fakestore.ui.screens.Home.ui.theme.Secondary

@Composable
fun DetailsUi(productId: Int?, cartViewModel: CartViewModel? = null) {
    cartViewModel?.let { viewModel ->
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            viewModel.initializeRepository(context)
        }
    }

    var product by remember { mutableStateOf<Products?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        try {
            isLoading = true
            error = null
            if (productId != null) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiSerice.getProductById(productId)
                }
                product = response
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }

        error != null -> Text(text = "Error: $error", modifier = Modifier.padding(16.dp))
        product != null -> Ui(product!!, cartViewModel)
        else -> Text(text = "Product not found", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun Ui(product: Products, cartViewModel: CartViewModel?) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Secondary,
                contentColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            LazyColumn ( modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)) {


         item {
             Image(
                 painter = rememberAsyncImagePainter(model = product.images.firstOrNull()),
                 contentDescription = product.title,
                 contentScale = ContentScale.Crop,
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(300.dp)
                     .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                     .clip(RoundedCornerShape(16.dp)),
             )
             Spacer(modifier = Modifier.height(20.dp))
             Text(
                 text = "Category: ${product.category.name}",
                 style = TextStyle(
                     color = Color.LightGray,
                     fontSize = 16.sp,
                     fontWeight = FontWeight.Normal
                 ),
                 modifier = Modifier.padding(bottom = 8.dp)
             )
             Text(
                 product.title,
                 style = TextStyle(
                     color = Color.White,
                     fontSize = 26.sp,
                     lineHeight = 30.sp,
                     fontWeight = FontWeight.W600
                 ),
                 maxLines = 3
             )
             Spacer(modifier = Modifier.height(8.dp))
             Text(
                 text = "${product.price} EGP",
                 style = TextStyle(
                     fontSize = 22.sp,
                     color = Goldy,
                     fontWeight = FontWeight.Thin,
                 ),
                 modifier = Modifier.padding(bottom = 10.dp)
             )
             Text(
                 product.description,
                 style = MaterialTheme.typography.bodyMedium,
                 textAlign = TextAlign.Justify,
                 modifier = Modifier.padding(bottom = 16.dp)
             )

             OutlinedButton(
                 onClick = {
                     cartViewModel?.addToCart(product)
                     scope.launch {
                         snackbarHostState.showSnackbar(
                             message = "${product.title} added to cart!",
                             duration = SnackbarDuration.Short
                         )
                     }
                 },
                 colors = ButtonDefaults.buttonColors(
                     containerColor = Color(0xFF323644),
                     contentColor = Color.White
                 ),
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(48.dp),
                 enabled = cartViewModel != null
             ) {
                 Icon(
                     painter = painterResource(id = R.drawable.cart_ico),
                     contentDescription = null,
                     modifier = Modifier.size(18.dp)
                 )
                 Spacer(Modifier.width(8.dp))
                 Text(text = "Add to Cart")
             }
         }
            }
         }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

