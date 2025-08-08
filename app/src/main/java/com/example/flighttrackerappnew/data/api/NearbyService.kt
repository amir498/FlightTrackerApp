package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import retrofit2.http.GET

interface NearbyService {
    @GET("nearby")
    suspend fun getNearby(
    ): List<NearByAirportsDataItems>
}

