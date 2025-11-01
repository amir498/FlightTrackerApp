package com.example.flighttrackerappnew.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FutureScheduleFlightService {
    @GET("flightsFuture")
    suspend fun getSchedulesFlight(
        @Query("type") type: String,
        @Query("iataCode") iataCode: String,
        @Query("date") date: String,
        @Query("key") apiKey: String
    ): Response<ResponseBody>
}