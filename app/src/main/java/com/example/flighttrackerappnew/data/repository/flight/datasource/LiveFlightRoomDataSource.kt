package com.example.flighttrackerappnew.data.repository.flight.datasource

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem

interface LiveFlightRoomDataSource {
    suspend fun getLiveFlightFromRoom(): List<FlightDataItem>
    suspend fun saveLiveFlightToRoom(flightDataItems: List<FlightDataItem>)
}