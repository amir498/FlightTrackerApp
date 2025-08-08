package com.example.flighttrackerappnew.data.repository.airports.datasource

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems

interface AirPortsRoomDataSource {
    suspend fun getAirportsFromRoom(): List<AirportsDataItems>
    suspend fun saveAirportsToRoom(dataFromRemote: List<AirportsDataItems>)
}