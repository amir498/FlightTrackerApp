package com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSourceImpl

import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightCacheDataSource

class ScheduleFlightCacheDataSourceImpl:ScheduleFlightCacheDataSource {
    private var scheduleFlightList = ArrayList<SchedulesFlightsItems>()

    override suspend fun getLiveFlightFromCache(): List<SchedulesFlightsItems> {
        return scheduleFlightList
    }

    override fun saveLiveFlightToCache(scheduleFlightList: List<SchedulesFlightsItems>) {
        this.scheduleFlightList = scheduleFlightList as ArrayList<SchedulesFlightsItems>
    }
}