package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import retrofit2.http.GET
import retrofit2.http.Query

interface StaticAirLineService {
    @GET("airlineDatabase")
    suspend fun getStaticAirLine(
        @Query("key") apiKey: String
    ): List<StaticAirLineItems>
}