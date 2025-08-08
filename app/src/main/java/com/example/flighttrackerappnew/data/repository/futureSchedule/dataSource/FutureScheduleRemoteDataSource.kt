package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource

import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem

interface FutureScheduleRemoteDataSource {
    suspend fun getFutureFlightData(): List<FutureScheduleItem>
}