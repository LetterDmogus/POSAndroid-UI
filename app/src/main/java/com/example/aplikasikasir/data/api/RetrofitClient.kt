package com.example.aplikasikasir.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    /**
     * Karena kamu menggunakan Laravel Herd langsung:
     * - Herd berjalan di port 80 (default HTTP).
     * - Emulator mengakses host lewat 10.0.2.2.
     */
    private const val BASE_URL = "http://10.0.2.2/api/"

    // Menambah durasi timeout agar tidak gagal koneksi dalam 1 detik
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}