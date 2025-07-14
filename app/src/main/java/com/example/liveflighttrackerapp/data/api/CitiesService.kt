package com.example.liveflighttrackerapp.data.api

import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import retrofit2.http.GET

interface CitiesService {
    @GET("cityDatabase")
    suspend fun getCities(
    ): List<CitiesDataItems>
}