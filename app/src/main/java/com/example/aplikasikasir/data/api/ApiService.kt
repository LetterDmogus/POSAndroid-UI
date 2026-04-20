package com.example.aplikasikasir.data.api

import com.example.aplikasikasir.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    fun login(@Body body: Map<String, String>): Call<LoginResponse>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<ApiResponse<Unit>>

    @GET("dashboard")
    fun getDashboard(@Header("Authorization") token: String): Call<DashboardResponse>

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

    @Multipart
    @POST("barangs")
    fun createBarang(
        @Header("Authorization") token: String,
        @Part("sku") sku: RequestBody,
        @Part("nama_barang") nama: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("stok") stok: RequestBody,
        @Part("satuan") satuan: RequestBody,
        @Part("harga_beli") hargaBeli: RequestBody,
        @Part("harga_jual") hargaJual: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part foto: MultipartBody.Part?
    ): Call<ApiResponse<Barang>>

    @Multipart
    @POST("barangs/{id}?_method=PUT") // Laravel workaround untuk Multipart PUT
    fun updateBarang(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("sku") sku: RequestBody,
        @Part("nama_barang") nama: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("stok") stok: RequestBody,
        @Part("satuan") satuan: RequestBody,
        @Part("harga_beli") hargaBeli: RequestBody,
        @Part("harga_jual") hargaJual: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part foto: MultipartBody.Part?
    ): Call<ApiResponse<Barang>>

    @DELETE("barangs/{id}")
    fun deleteBarang(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<ApiResponse<Unit>>

    @GET("categories")
    fun getCategories(@Header("Authorization") token: String): Call<ApiResponse<List<Category>>>

    @POST("categories")
    fun createCategory(@Header("Authorization") token: String, @Body body: Map<String, String>): Call<ApiResponse<Category>>

    @PUT("categories/{id}")
    fun updateCategory(@Header("Authorization") token: String, @Path("id") id: Int, @Body body: Map<String, String>): Call<ApiResponse<Category>>

    @DELETE("categories/{id}")
    fun deleteCategory(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Unit>>

    @POST("orders")
    fun createOrder(@Header("Authorization") token: String, @Body body: OrderRequest): Call<OrderResponse>

    @GET("orders")
    fun getOrders(@Header("Authorization") token: String): Call<OrderHistoryResponse>

    @GET("invoice/{nomor_invoice}")
    fun getInvoice(@Header("Authorization") token: String, @Path("nomor_invoice") nomorInvoice: String): Call<InvoiceResponse>
}