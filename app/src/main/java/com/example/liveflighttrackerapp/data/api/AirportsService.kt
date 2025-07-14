package com.example.liveflighttrackerapp.data.api

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import retrofit2.http.GET

interface AirportsService {
    @GET("airportDatabase")
    suspend fun getAirportsLine(
    ): List<AirportsDataItems>
}