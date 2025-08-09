package com.elm.fakestore.data.network

import com.elm.fakestore.data.models.Category
import com.elm.fakestore.data.models.Products
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiSerice {
@GET("products")
suspend fun getProducts(
    @Query("offset") offset: Int = 0,
    @Query("limit") limit: Int = 10
): List<Products>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int?): Products
    
    @GET("categories")
    suspend fun getCategories(): List<Category>
    
    @GET("categories/{id}/products")
    suspend fun getCategoryProducts(
        @Path("id") categoryId: Int,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): List<Products>
}