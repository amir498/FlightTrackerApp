package com.example.flighttrackerappnew.data.repository.flight.datasourceImpl

import com.example.flighttrackerappnew.data.api.FlightApiService
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

class LiveFlightRemoteDataSourceImpl(private val flightApiService: FlightApiService): LiveFlightRemoteDataSource {
    val apiKey = RemoteConfigManager.getString("api_key")
    override suspend fun getLiveFlightData(latitude: Double, longitude: Double, distance: Int): List<FlightDataItem>  = flightApiService.getFlights(latitude,longitude,distance,apiKey)
}