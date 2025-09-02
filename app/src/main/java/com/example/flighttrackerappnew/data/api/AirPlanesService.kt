package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import retrofit2.http.GET
import retrofit2.http.Query

interface AirPlanesService {
    @GET("airplaneDatabase")
    suspend fun getAirPlanesData(
        @Query("key") apiKey: String
    ): List<AirPlaneItems>
}