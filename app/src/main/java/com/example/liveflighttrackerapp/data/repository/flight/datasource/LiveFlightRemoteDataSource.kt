package com.example.liveflighttrackerapp.data.repository.flight.datasource

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem

interface LiveFlightRemoteDataSource {
    suspend fun getLiveFlightData(): List<FlightDataItem>
}