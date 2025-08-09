package com.elm.fakestore.ui.screens.deitals

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.elm.fakestore.data.models.Products
import com.elm.fakestore.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun DetailsUi(productId: Int?){
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

    when{
        isLoading -> Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
        error != null -> Text(text = "Error: $error", modifier = Modifier.padding(16.dp))
        product != null -> Ui(product!!)
        else -> Text(text = "Product not found", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun Ui(product: Products) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = product.images.firstOrNull()),
            contentDescription = product.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Category: ${product.category.name}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )

            Text(
                text = product.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

        Row {
            Text(
                text = "Price: ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = product.price.toString() + " EGP",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
        Text(text = product.description, style = MaterialTheme.typography.bodyMedium,  modifier =    Modifier.padding(8.dp))


        Button(
            onClick = { /* Handle add to cart action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Add to Cart", modifier = Modifier.padding(8.dp))
        }
    }
}

