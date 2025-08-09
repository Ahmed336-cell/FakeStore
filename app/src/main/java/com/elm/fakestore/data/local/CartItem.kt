package com.elm.fakestore.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val title: String,
    val price: Int,
    val image: String,
    val quantity: Int = 1
)
