package com.example.aplikasikasir.data.api

import android.content.Context
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://luiz.rplkodingan.com/posandroid/api/"

    /**
     * Interceptor untuk setup header request.
     * Mengatur Host hanya saat lokal emulator, dan menambahkan bypass warning ngrok.
     */
    private val hostInterceptor = Interceptor { chain ->
        var request = chain.request()
        val originalUrl = request.url()

        val requestBuilder = request.newBuilder()
            // WAJIB: Bypass halaman warning bawaan ngrok free tier
            .header("ngrok-skip-browser-warning", "true")

        // Set Host Header hanya jika mengarah ke lokal/emulator test
        if (originalUrl.host() == "apipos.test" || originalUrl.host() == "10.0.2.2") {
            requestBuilder.header("Host", "apipos.test")
        }

        chain.proceed(requestBuilder.build())
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

    fun initPicasso(context: Context) {
        try {
            val picasso = Picasso.Builder(context)
                .downloader(OkHttp3Downloader(okHttpClient))
                .build()
            
            Picasso.setSingletonInstance(picasso)
        } catch (e: Exception) {
            // Jika sudah diinisialisasi, abaikan
        }
    }
}