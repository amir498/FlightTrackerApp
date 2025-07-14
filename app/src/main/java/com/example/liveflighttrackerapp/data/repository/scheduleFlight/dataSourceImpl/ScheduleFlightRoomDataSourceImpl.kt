package com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSourceImpl

import com.example.liveflighttrackerapp.data.db.SchedulesFlightDao
import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightRoomDataSource

class ScheduleFlightRoomDataSourceImpl(private val scheduleFlightDao: SchedulesFlightDao) : ScheduleFlightRoomDataSource {
    override suspend fun getLiveFlightFromRoom(): List<SchedulesFlightsItems> {
        return scheduleFlightDao.getSchedulesFlightData()
    }

    override suspend fun saveLiveFlightToRoom(flightDataItems: List<SchedulesFlightsItems>) {
        scheduleFlightDao.insertSchedulesFlightData(flightDataItems)
    }
}