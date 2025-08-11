package com.example.liveflighttrackerapp.data.api

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import retrofit2.http.GET

interface FlightApiService {
    @GET("flights")
    suspend fun getFlights(
    ):List<FlightDataItem>
}
