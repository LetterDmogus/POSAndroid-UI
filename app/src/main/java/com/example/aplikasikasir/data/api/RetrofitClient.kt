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
    private const val BASE_URL = "http://10.0.2.2/api/"

    /**
     * Interceptor Pintar: 
     * Menggunakan fungsi .url() dan .host() agar kompatibel dengan semua versi OkHttp.
     */
    private val hostInterceptor = Interceptor { chain ->
        var request = chain.request()
        val originalUrl = request.url()

        // Cek jika host-nya adalah apipos.test
        if (originalUrl.host() == "apipos.test") {
            val newUrl = originalUrl.newBuilder()
                .host("10.0.2.2")
                .build()
            
            request = request.newBuilder()
                .url(newUrl)
                .header("Host", "apipos.test")
                .build()
        } else {
            // Untuk semua request ke API (10.0.2.2), pastikan Host Header terpasang
            request = request.newBuilder()
                .header("Host", "apipos.test")
                .build()
        }

        chain.proceed(request)
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
            
            // Picasso.setSingletonInstance hanya boleh dipanggil satu kali
            Picasso.setSingletonInstance(picasso)
        } catch (e: Exception) {
            // Jika sudah diinisialisasi, abaikan
        }
    }
}