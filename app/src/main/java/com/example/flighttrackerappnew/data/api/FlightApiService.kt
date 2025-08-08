package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import retrofit2.http.GET

interface FlightApiService {
    @GET("flights")
    suspend fun getFlights(
    ):List<FlightDataItem>
}
