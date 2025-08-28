package com.example.flighttrackerappnew.data.repository.flight.datasourceImpl

import com.example.flighttrackerappnew.data.api.FlightApiService
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightRemoteDataSource

class LiveFlightRemoteDataSourceImpl(private val flightApiService: FlightApiService): LiveFlightRemoteDataSource {
    override suspend fun getLiveFlightData(latitude: Double, longitude: Double, distance: Int): List<FlightDataItem>  = flightApiService.getFlights(latitude,longitude,distance)
}