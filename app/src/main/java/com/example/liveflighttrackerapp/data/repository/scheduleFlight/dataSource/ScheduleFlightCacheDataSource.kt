package com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource

import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems

interface ScheduleFlightCacheDataSource {
    suspend fun getLiveFlightFromCache(): List<SchedulesFlightsItems>
    fun saveLiveFlightToCache(scheduleFlightList: List<SchedulesFlightsItems>)
}