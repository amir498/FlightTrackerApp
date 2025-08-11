package com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSourceImpl

import com.example.liveflighttrackerapp.data.api.SchedulesFlightService
import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightRemoteDataSource

class ScheduleFlightRemoteDataSourceImpl(
    private val scheduleFlightService: SchedulesFlightService
):ScheduleFlightRemoteDataSource {
    override suspend fun getLiveFlightData(): List<SchedulesFlightsItems> {
        return scheduleFlightService.getSchedulesFlight()
    }
}