package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource

import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem

interface FutureScheduleRoomDataSource {
    suspend fun getFutureFlightFromRoom(): List<FutureScheduleItem>
    suspend fun saveFutureFlightToRoom(flightDataItems: List<FutureScheduleItem>)
}