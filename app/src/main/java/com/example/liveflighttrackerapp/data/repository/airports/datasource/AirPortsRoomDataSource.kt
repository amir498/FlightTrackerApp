package com.example.liveflighttrackerapp.data.repository.airports.datasource

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems

interface AirPortsRoomDataSource {
    suspend fun getAirportsFromRoom(): List<AirportsDataItems>
    suspend fun saveAirportsToRoom(dataFromRemote: List<AirportsDataItems>)
}