package com.example.flighttrackerappnew.data.repository.airports.datasource

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems

interface AirPortsRemoteDataSource {
    suspend fun getAirPortsFromRemote(): List<AirportsDataItems>
}