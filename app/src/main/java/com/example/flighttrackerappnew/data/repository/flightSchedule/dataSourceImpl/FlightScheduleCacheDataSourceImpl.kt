package com.example.flighttrackerappnew.data.repository.flightSchedule.dataSourceImpl

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleCacheDataSource

class FlightScheduleCacheDataSourceImpl:FlightScheduleCacheDataSource {
    private var scheduleFlightList = ArrayList<FlightSchedulesItems>()

    override suspend fun getLiveFlightFromCache(): List<FlightSchedulesItems> {
        return scheduleFlightList
    }

    override fun saveLiveFlightToCache(scheduleFlightList: List<FlightSchedulesItems>) {
        this.scheduleFlightList = scheduleFlightList as ArrayList<FlightSchedulesItems>
    }
}