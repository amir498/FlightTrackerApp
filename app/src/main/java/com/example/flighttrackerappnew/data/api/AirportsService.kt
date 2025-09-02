package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import retrofit2.http.GET
import retrofit2.http.Query

interface AirportsService {
    @GET("airportDatabase")
    suspend fun getAirportsLine(
        @Query("key") apiKey: String
    ): List<AirportsDataItems>
}