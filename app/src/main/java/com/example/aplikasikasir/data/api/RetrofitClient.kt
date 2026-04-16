package com.example.aplikasikasir.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Tetap gunakan IP alias Emulator
    private const val BASE_URL = "http://10.0.2.2/api/"

    // Interceptor untuk memaksa Herd mengenali project "apipos.test"
    private val hostInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestWithHost = originalRequest.newBuilder()
            .header("Host", "apipos.test") // MENGARAHKAN KE PROJECT YANG BENAR
            .build()
        chain.proceed(requestWithHost)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(hostInterceptor)
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