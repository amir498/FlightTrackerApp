package com.example.flighttrackerappnew.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CitiesService {
    @GET("cityDatabase")
    suspend fun getCities(
        @Query("key") apiKey: String
    ): Response<ResponseBody>
}
