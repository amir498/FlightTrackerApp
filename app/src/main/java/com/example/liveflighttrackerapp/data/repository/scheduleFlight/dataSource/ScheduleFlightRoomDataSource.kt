package com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource

import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems

interface ScheduleFlightRoomDataSource {
    suspend fun getLiveFlightFromRoom(): List<SchedulesFlightsItems>
    suspend fun saveLiveFlightToRoom(flightDataItems: List<SchedulesFlightsItems>)
}