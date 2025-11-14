package com.example.flighttrackerappnew.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightSchedulesServiceForSpecificiataNo {
    @GET("timetable")
    suspend fun getSchedulesFlightForSpecificIataNo(
        @Query("key") apiKey: String,
        @Query("flight_iata") flightIataNo: String,
    ): Response<ResponseBody>
}