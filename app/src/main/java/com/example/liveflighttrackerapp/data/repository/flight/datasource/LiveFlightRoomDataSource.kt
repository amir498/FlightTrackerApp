package com.example.liveflighttrackerapp.data.repository.flight.datasource

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem

interface LiveFlightRoomDataSource {
    suspend fun getLiveFlightFromRoom(): List<FlightDataItem>
    suspend fun saveLiveFlightToRoom(flightDataItems: List<FlightDataItem>)
}