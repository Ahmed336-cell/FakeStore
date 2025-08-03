package com.elm.fakestore.network

import com.elm.fakestore.data.Products
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiSerice {
@GET("products")
suspend fun getProducts(): List<Products>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int?): Products
}