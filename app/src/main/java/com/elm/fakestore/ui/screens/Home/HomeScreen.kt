package com.elm.fakestore.ui.screens.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.elm.fakestore.R
import com.elm.fakestore.data.models.Products
import com.elm.fakestore.ui.navigationBar.Screens
import com.elm.fakestore.ui.screens.Home.ui.theme.Goldy
import com.elm.fakestore.ui.screens.Home.ui.theme.Secondary
import com.elm.fakestore.ui.viewModel.CartViewModel
import com.elm.fakestore.ui.viewModel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    cartViewModel: CartViewModel? = null
) {
    cartViewModel?.let { cartVM ->
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            cartVM.initializeRepository(context)
        }
    }
    val products by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.refreshProducts()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
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

                else -> proudctList(
                    products = products,
                    isLoading = isLoading,
                    onProductClick = { product ->
                        navController.navigate(Screens.Details.createRoute(product.id))
                    },
                    onLoadMore = {
                        viewModel.loadMoreProducts()
                    },
                    cartViewModel = cartViewModel,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
fun proudctList(
    products: List<Products>,
    isLoading: Boolean,
    onProductClick: (Products) -> Unit,
    onLoadMore: () -> Unit,
    cartViewModel: CartViewModel? = null,
    snackbarHostState: SnackbarHostState
) {
    if (products.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = "No products available", modifier = Modifier.padding(16.dp))
        }
    } else {
        LazyColumn {
            items(products) { product ->
                ProductCard(
                    product = product,
                    cartViewModel = cartViewModel,
                    snackbarHostState = snackbarHostState,
                    onClick = { onProductClick(product) }
                )
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
                            Button(onClick = onLoadMore) {
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
fun ProductCard(
    product: Products,
    cartViewModel: CartViewModel? = null,
    snackbarHostState: SnackbarHostState,
    onClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Secondary,
            contentColor = Color.White
        ),
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
                    .size(128.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.FillBounds,
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = product.title,
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W600
                    ),
                    maxLines = 2
                )
                Text(
                    "${product.price} EGP",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Goldy,
                        fontWeight = FontWeight.Thin
                    )
                )
                AddToCartButton(
                    product = product,
                    cartViewModel = cartViewModel,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AddToCartButton(
    product: Products,
    cartViewModel: CartViewModel?,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = {
            if (cartViewModel != null && !isLoading) {
                isLoading = true
                scope.launch {
                    try {
                        cartViewModel.addToCart(product)
                        snackbarHostState.showSnackbar(
                            message = "${product.title} added to cart!",
                            duration = SnackbarDuration.Short
                        )
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(
                            message = "Failed to add ${product.title} to cart.",
                            duration = SnackbarDuration.Short
                        )
                    } finally {
                        isLoading = false
                    }
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF323644),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(50),
        modifier = modifier

            .semantics { contentDescription = "Add ${product.title} to cart" },
        enabled = cartViewModel != null && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
            )
        } else {
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