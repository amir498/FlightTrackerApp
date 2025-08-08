package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource

import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem

interface FutureScheduleCacheDataSource {
    suspend fun getFutureFlightFromCache(): List<FutureScheduleItem>
    fun saveFutureFlightToCache(scheduleFlightList: List<FutureScheduleItem>)
}