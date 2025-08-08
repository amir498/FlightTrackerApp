package com.example.flighttrackerappnew.data.repository.flight.datasource

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem

interface LiveFlightRemoteDataSource {
    suspend fun getLiveFlightData(): List<FlightDataItem>
}