package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import retrofit2.http.GET
import retrofit2.http.Query

interface FutureScheduleFlightService {
    @GET("flightsFuture")
    suspend fun getSchedulesFlight(
        @Query("type") type: String,
        @Query("iataCode") iataCode: String,
        @Query("date") date: String
    ): List<FutureScheduleItem>
}