package com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource

import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems

interface ScheduleFlightRemoteDataSource {
    suspend fun getLiveFlightData(): List<SchedulesFlightsItems>
}