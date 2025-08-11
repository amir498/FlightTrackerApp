package com.example.liveflighttrackerapp.data.api

import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import retrofit2.http.GET

interface AirPlanesService {
    @GET("airplaneDatabase")
    suspend fun getAirPlanesData(
    ): List<AirPlaneItems>
}