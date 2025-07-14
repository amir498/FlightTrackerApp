package com.example.liveflighttrackerapp.data.repository.airports.datasource

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems

interface AirPortsRemoteDataSource {
    suspend fun getAirPortsFromRemote(): List<AirportsDataItems>
}