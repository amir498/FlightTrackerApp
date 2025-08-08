package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import retrofit2.http.GET

interface AirportsService {
    @GET("airportDatabase")
    suspend fun getAirportsLine(
    ): List<AirportsDataItems>
}