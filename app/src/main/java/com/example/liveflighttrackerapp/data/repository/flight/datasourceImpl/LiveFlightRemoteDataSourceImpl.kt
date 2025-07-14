package com.example.liveflighttrackerapp.data.repository.flight.datasourceImpl

import com.example.liveflighttrackerapp.data.api.FlightApiService
import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.data.repository.flight.datasource.LiveFlightRemoteDataSource

class LiveFlightRemoteDataSourceImpl(private val flightApiService: FlightApiService): LiveFlightRemoteDataSource {
    override suspend fun getLiveFlightData(): List<FlightDataItem>  = flightApiService.getFlights()
}