package com.example.flighttrackerappnew.data.repository.flightSchedule.dataSourceImpl

import com.example.flighttrackerappnew.data.api.FlightSchedulesService
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

class FlightScheduleRemoteDataSourceImpl(
    private val scheduleFlightService: FlightSchedulesService
):FlightScheduleRemoteDataSource {
    override suspend fun getLiveFlightData(): List<FlightSchedulesItems> {
        val apiKey = RemoteConfigManager.getString("api_key")
        return scheduleFlightService.getSchedulesFlight(apiKey)
    }
}