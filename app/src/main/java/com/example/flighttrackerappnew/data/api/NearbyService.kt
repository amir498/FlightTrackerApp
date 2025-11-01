package com.example.flighttrackerappnew.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbyService {
    @GET("nearby")
    suspend fun getNearby(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("distance") distance: Int,
        @Query("key") apiKey: String
    ): Response<ResponseBody>
}

