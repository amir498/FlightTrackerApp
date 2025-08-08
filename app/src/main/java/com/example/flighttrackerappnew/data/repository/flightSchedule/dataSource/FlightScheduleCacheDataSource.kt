package com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems

interface FlightScheduleCacheDataSource {
    suspend fun getLiveFlightFromCache(): List<FlightSchedulesItems>
    fun saveLiveFlightToCache(scheduleFlightList: List<FlightSchedulesItems>)
}