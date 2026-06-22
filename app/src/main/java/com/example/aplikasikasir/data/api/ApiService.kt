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

    @GET("items")
    fun getBarangs(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Int? = null
    ): Call<ApiResponse<List<Barang>>>

    @GET("items/scan/{sku}")
    fun scanBarang(
        @Header("Authorization") token: String,
        @Path("sku") sku: String
    ): Call<ApiResponse<Barang>>

    @Multipart
    @POST("items")
    fun createBarang(
        @Header("Authorization") token: String,
        @Part("sku") sku: RequestBody,
        @Part("name") nama: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("stock") stok: RequestBody,
        @Part("unit") satuan: RequestBody,
        @Part("purchase_price") hargaBeli: RequestBody,
        @Part("selling_price") hargaJual: RequestBody,
        @Part("description") deskripsi: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<ApiResponse<Barang>>

    @Multipart
    @POST("items/{id}?_method=PUT") // Laravel workaround untuk Multipart PUT
    fun updateBarang(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("sku") sku: RequestBody,
        @Part("name") nama: RequestBody,
        @Part("category_id") categoryId: RequestBody,
        @Part("stock") stok: RequestBody,
        @Part("unit") satuan: RequestBody,
        @Part("purchase_price") hargaBeli: RequestBody,
        @Part("selling_price") hargaJual: RequestBody,
        @Part("description") deskripsi: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<ApiResponse<Barang>>

    @DELETE("items/{id}")
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
    fun getOrders(
        @Header("Authorization") token: String,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Call<OrderHistoryResponse>

    @GET("invoice/{nomor_invoice}")
    fun getInvoice(@Header("Authorization") token: String, @Path("nomor_invoice") nomorInvoice: String): Call<InvoiceResponse>

    @GET("customers")
    fun getCustomers(@Header("Authorization") token: String): Call<ApiResponse<List<Customer>>>

    @POST("customers")
    fun createCustomer(@Header("Authorization") token: String, @Body body: Map<String, String>): Call<ApiResponse<Customer>>

    @PUT("customers/{id}")
    fun updateCustomer(@Header("Authorization") token: String, @Path("id") id: Int, @Body body: Map<String, String>): Call<ApiResponse<Customer>>

    @DELETE("customers/{id}")
    fun deleteCustomer(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Unit>>

    @GET("transactions")
    fun getTransactions(@Header("Authorization") token: String): Call<TransactionResponse>

    @POST("transactions")
    fun createTransaction(@Header("Authorization") token: String, @Body body: Map<String, String>): Call<ApiResponse<Transaction>>

    @DELETE("transactions/{id}")
    fun deleteTransaction(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Unit>>

    // Operations for Discount Management
    @GET("discounts")
    fun getDiscounts(@Header("Authorization") token: String): Call<ApiResponse<List<Discount>>>

    @POST("discounts")
    fun createDiscount(@Header("Authorization") token: String, @Body body: Map<String, String>): Call<ApiResponse<Discount>>

    @PUT("discounts/{id}")
    fun updateDiscount(@Header("Authorization") token: String, @Path("id") id: Int, @Body body: Map<String, String>): Call<ApiResponse<Discount>>

    @DELETE("discounts/{id}")
    fun deleteDiscount(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Unit>>

    // Operations for Stock Entry (Barang Masuk) Management
    @GET("stock-entries")
    fun getStockEntries(@Header("Authorization") token: String): Call<ApiResponse<List<StockEntry>>>

    @POST("stock-entries")
    fun createStockEntry(@Header("Authorization") token: String, @Body body: Map<String, String>): Call<ApiResponse<StockEntry>>

    @DELETE("stock-entries/{id}")
    fun deleteStockEntry(@Header("Authorization") token: String, @Path("id") id: Int): Call<ApiResponse<Unit>>
}