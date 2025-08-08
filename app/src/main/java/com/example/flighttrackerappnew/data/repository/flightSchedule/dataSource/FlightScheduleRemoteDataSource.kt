package com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems

interface FlightScheduleRemoteDataSource {
    suspend fun getLiveFlightData(): List<FlightSchedulesItems>
}