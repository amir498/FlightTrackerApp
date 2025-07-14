package com.example.liveflighttrackerapp.data.api

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import retrofit2.http.GET

interface StaticAirLineService {
    @GET("airlineDatabase")
    suspend fun getStaticAirLine(
    ): List<StaticAirLineItems>
}