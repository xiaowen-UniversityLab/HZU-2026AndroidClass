package com.example.downloadpicture.model

import com.example.downloadpicture.network.DownloadRetrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProvideRetrofit {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://picsum.photos/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val downloadApi: DownloadRetrofit by lazy {
        retrofit.create(DownloadRetrofit::class.java)
    }
}