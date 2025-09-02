package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import retrofit2.http.GET
import retrofit2.http.Query

interface CitiesService {
    @GET("cityDatabase")
    suspend fun getCities(
        @Query("key") apiKey: String
    ): List<CitiesDataItems>
}