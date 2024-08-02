package com.example.guru_app_.popapi

import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

// RetrofitClient.kt
object RetrofitClient {
    private const val BASE_URL = "http://www.aladin.co.kr/ttb/api/"

    val instance: AladinApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()

        retrofit.create(AladinApi::class.java)
    }
}

