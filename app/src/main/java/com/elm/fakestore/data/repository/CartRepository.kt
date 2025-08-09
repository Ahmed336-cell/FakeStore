package com.elm.fakestore.data.repository

import com.elm.fakestore.data.local.CartDao
import com.elm.fakestore.data.local.CartItem
import com.elm.fakestore.data.models.Products
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {
    
    fun getAllCartItems(): Flow<List<CartItem>> = cartDao.getAllCartItems()
    
    fun getCartItemCount(): Flow<Int> = cartDao.getCartItemCount()
    
    fun getTotalPrice(): Flow<Int?> = cartDao.getTotalPrice()
    
    suspend fun addToCart(product: Products) {
        val existingItem = cartDao.getCartItemByProductId(product.id)
        if (existingItem != null) {
            // If item exists, increase quantity
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            cartDao.updateCartItem(updatedItem)
        } else {
            // If item doesn't exist, add new item
            val cartItem = CartItem(
                productId = product.id,
                title = product.title,
                price = product.price,
                image = product.images.firstOrNull() ?: ""
            )
            cartDao.insertCartItem(cartItem)
        }
    }
    
    suspend fun updateQuantity(productId: Int, quantity: Int) {
        val existingItem = cartDao.getCartItemByProductId(productId)
        existingItem?.let {
            if (quantity <= 0) {
                cartDao.deleteCartItem(it)
            } else {
                val updatedItem = it.copy(quantity = quantity)
                cartDao.updateCartItem(updatedItem)
            }
        }
    }
    
    suspend fun removeFromCart(productId: Int) {
        cartDao.deleteCartItemByProductId(productId)
    }
    
    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
