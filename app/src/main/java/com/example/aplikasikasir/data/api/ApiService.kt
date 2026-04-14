package com.example.aplikasikasir.data.api

import com.example.aplikasikasir.data.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    fun login(@Body body: Map<String, String>): Call<LoginResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<ApiResponse<Unit>>

    @GET("barangs")
    fun getBarangs(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null
    ): Call<ApiResponse<List<Barang>>>

    @GET("barangs/scan/{sku}")
    fun scanBarang(
        @Header("Authorization") token: String,
        @Path("sku") sku: String
    ): Call<ApiResponse<Barang>>

    @GET("categories")
    fun getCategories(@Header("Authorization") token: String): Call<ApiResponse<List<Category>>>

    // Kamu bisa tambahkan untuk POST Order juga di sini
    @POST("orders")
    fun createOrder(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): Call<ApiResponse<Any>>
}