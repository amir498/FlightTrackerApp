package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl

import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleCacheDataSource

class FutureScheduleCacheDataSourceImpl : FutureScheduleCacheDataSource {
    private var futureScheduleList = mutableListOf<FutureScheduleItem>()

    override suspend fun getFutureFlightFromCache(): List<FutureScheduleItem> {
        return futureScheduleList
    }

    override fun saveFutureFlightToCache(scheduleFlightList: List<FutureScheduleItem>) {
        futureScheduleList = ArrayList(scheduleFlightList)
    }
}