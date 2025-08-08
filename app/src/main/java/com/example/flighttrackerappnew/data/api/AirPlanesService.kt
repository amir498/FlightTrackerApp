package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import retrofit2.http.GET

interface AirPlanesService {
    @GET("airplaneDatabase")
    suspend fun getAirPlanesData(
    ): List<AirPlaneItems>
}