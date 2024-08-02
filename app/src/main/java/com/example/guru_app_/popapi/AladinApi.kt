package com.example.guru_app_.popapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// AladinApiService.kt
interface AladinApi {
    @GET("ItemList.aspx")
    fun getBestsellers(
        @Query("ttbkey") apiKey: String,
        @Query("QueryType") queryType: String,
        @Query("MaxResults") maxResults: Int,
        @Query("start") start: Int,
        @Query("SearchTarget") searchTarget: String,
        @Query("output") output: String = "xml",
        @Query("Version") version: String = "20131101"
    ): Call<BestsellerResponse>
}
