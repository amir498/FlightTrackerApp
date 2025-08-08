package com.example.flighttrackerappnew.data.repository.flightSchedule.dataSourceImpl

import com.example.flighttrackerappnew.data.db.FlightSchedulesDao
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRoomDataSource

class FlightScheduleRoomDataSourceImpl(private val scheduleFlightDao: FlightSchedulesDao) : FlightScheduleRoomDataSource {
    override suspend fun getLiveFlightFromRoom(): List<FlightSchedulesItems> {
        return scheduleFlightDao.getSchedulesFlightData()
    }

    override suspend fun saveLiveFlightToRoom(flightDataItems: List<FlightSchedulesItems>) {
        scheduleFlightDao.insertSchedulesFlightData(flightDataItems)
    }
}