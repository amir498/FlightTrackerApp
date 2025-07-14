package com.example.liveflighttrackerapp.data.api

import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems
import retrofit2.http.GET

interface NearbyService {
    @GET("nearby")
    suspend fun getNearby(
    ): List<NearByAirportsDataItems>
}

