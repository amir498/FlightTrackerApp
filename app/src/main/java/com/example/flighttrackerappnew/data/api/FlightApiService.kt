package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightApiService {
    @GET("flights")
    suspend fun getFlights(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("distance") distance: Int,
        @Query("key") apiKey: String
    ): List<FlightDataItem>
}
