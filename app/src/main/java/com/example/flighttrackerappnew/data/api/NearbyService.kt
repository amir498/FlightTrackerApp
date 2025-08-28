package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbyService {
    @GET("nearby")
    suspend fun getNearby(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("distance") distance: Int
    ): List<NearByAirportsDataItems>
}

