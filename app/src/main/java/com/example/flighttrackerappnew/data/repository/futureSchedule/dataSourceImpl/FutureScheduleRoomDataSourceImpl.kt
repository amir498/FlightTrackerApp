package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl

import com.example.flighttrackerappnew.data.db.FutureSchedulesDao
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleRoomDataSource

class FutureScheduleRoomDataSourceImpl(
    private val futureScheduleDao: FutureSchedulesDao
) : FutureScheduleRoomDataSource {
    override suspend fun getFutureFlightFromRoom(): List<FutureScheduleItem> {
        return futureScheduleDao.getSchedulesFlightData()
    }

    override suspend fun saveFutureFlightToRoom(flightDataItems: List<FutureScheduleItem>) {
        futureScheduleDao.insertSchedulesFlightData(flightDataItems)
    }
}