package com.elm.fakestore.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elm.fakestore.data.local.AppDatabase
import com.elm.fakestore.data.local.CartItem
import com.elm.fakestore.data.models.Products
import com.elm.fakestore.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel() : ViewModel() {
    
    private var repository: CartRepository? = null
    
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()
    
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()
    
    private val _totalPrice = MutableStateFlow(0)
    val totalPrice: StateFlow<Int> = _totalPrice.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun initializeRepository(context: android.content.Context) {
        if (repository == null) {
            val database = AppDatabase.getDatabase(context)
            repository = CartRepository(database.cartDao())
            loadCartData()
        }
    }
    
    private fun loadCartData() {
        if (repository == null) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository!!.getAllCartItems().collect { items ->
                    _cartItems.value = items
                    _isLoading.value = false
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
        
        viewModelScope.launch {
            try {
                repository!!.getCartItemCount().collect { count ->
                    _cartItemCount.value = count
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
        
        viewModelScope.launch {
            try {
                repository!!.getTotalPrice().collect { total ->
                    _totalPrice.value = total ?: 0
                }
            } catch (e: Exception) {


            }
        }
    }
    
    fun addToCart(product: Products) {
        if (repository == null) return
        
        viewModelScope.launch {
            repository!!.addToCart(product)
            // Refresh cart data after adding item
            loadCartData()
        }
    }
    
    fun updateQuantity(productId: Int, quantity: Int) {
        if (repository == null) return
        
        viewModelScope.launch {
            repository!!.updateQuantity(productId, quantity)
            // Refresh cart data after updating quantity
            loadCartData()
        }
    }
    
    fun removeFromCart(productId: Int) {
        if (repository == null) return
        
        viewModelScope.launch {
            repository!!.removeFromCart(productId)
            // Refresh cart data after removing item
            loadCartData()
        }
    }
    
    fun clearCart() {
        if (repository == null) return
        
        viewModelScope.launch {
            repository!!.clearCart()
            // Refresh cart data after clearing cart
            loadCartData()
        }
    }
    
    fun refreshCart() {
        if (repository != null) {
            loadCartData()
        }
    }
    
    fun increaseQuantity(productId: Int) {
        val currentItem = _cartItems.value.find { it.productId == productId }
        currentItem?.let {
            updateQuantity(productId, it.quantity + 1)
        }
    }
    
    fun decreaseQuantity(productId: Int) {
        val currentItem = _cartItems.value.find { it.productId == productId }
        currentItem?.let {
            if (it.quantity > 1) {
                updateQuantity(productId, it.quantity - 1)
            } else {
                removeFromCart(productId)
            }
        }
    }
}

