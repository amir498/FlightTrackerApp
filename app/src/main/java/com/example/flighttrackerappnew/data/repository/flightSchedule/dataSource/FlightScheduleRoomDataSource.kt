package com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems

interface FlightScheduleRoomDataSource {
    suspend fun getLiveFlightFromRoom(): List<FlightSchedulesItems>
    suspend fun saveLiveFlightToRoom(flightDataItems: List<FlightSchedulesItems>)
}