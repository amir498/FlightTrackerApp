package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import retrofit2.http.GET

interface CitiesService {
    @GET("cityDatabase")
    suspend fun getCities(
    ): List<CitiesDataItems>
}