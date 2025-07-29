package com.example.miniproject.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton object สำหรับเก็บ Retrofit instance
object ApiClient {
    // Base URL for calling all APIs
    private const val BASE_URL = "http://10.0.2.2:8080/lazyhr/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
       .addInterceptor(loggingInterceptor)
       .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        instance.create(ApiService::class.java)
}
     }
