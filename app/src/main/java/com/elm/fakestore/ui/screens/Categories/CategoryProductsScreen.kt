package com.elm.fakestore.ui.screens.Categories

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.elm.fakestore.data.models.Products
import com.elm.fakestore.ui.navigationBar.Screens
import com.elm.fakestore.ui.viewModel.CategoryViewModel

@Composable
fun CategoryProductsScreen(
    categoryId: Int,
    categoryName: String,
    viewModel: CategoryViewModel,
    navController: NavHostController
) {
    val products by viewModel.categoryProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.fetchCategoryProducts(categoryId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Products in $categoryName",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        when {
            isLoading && products.isEmpty() -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
            errorMessage != null && products.isEmpty() -> Text(
                text = "Error: $errorMessage",
                modifier = Modifier.padding(16.dp)
            )
            else -> CategoryProductsList(
                products = products,
                isLoading = isLoading,
                onProductClick = { product ->
                    navController.navigate(Screens.Details.createRoute(product.id))
                },
                onLoadMore = {
                    viewModel.loadMoreCategoryProducts()
                }
            )
        }
    }
}

@Composable
fun CategoryProductsList(
    products: List<Products>,
    isLoading: Boolean,
    onProductClick: (Products) -> Unit,
    onLoadMore: () -> Unit
) {
    if (products.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = "No products available in this category", modifier = Modifier.padding(16.dp))
        }
    } else {
        LazyColumn {
            items(products) { product ->
                CategoryProductCard(product) {
                    onProductClick(product)
                }
            }
            
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
fun CategoryProductCard(product: Products, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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
                modifier = Modifier.padding(start = 16.dp)
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
        }
    }
}
