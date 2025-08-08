package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import retrofit2.http.GET

interface StaticAirLineService {
    @GET("airlineDatabase")
    suspend fun getStaticAirLine(
    ): List<StaticAirLineItems>
}