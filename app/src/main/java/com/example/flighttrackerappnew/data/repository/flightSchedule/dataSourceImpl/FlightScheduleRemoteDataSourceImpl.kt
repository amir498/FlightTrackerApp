package com.example.flighttrackerappnew.data.repository.flightSchedule.dataSourceImpl

import com.example.flighttrackerappnew.data.api.FlightSchedulesService
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRemoteDataSource

class FlightScheduleRemoteDataSourceImpl(
    private val scheduleFlightService: FlightSchedulesService
):FlightScheduleRemoteDataSource {
    override suspend fun getLiveFlightData(): List<FlightSchedulesItems> {
        return scheduleFlightService.getSchedulesFlight()
    }
}