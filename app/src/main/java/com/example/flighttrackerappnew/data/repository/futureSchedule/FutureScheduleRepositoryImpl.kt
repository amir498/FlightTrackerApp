package com.example.flighttrackerappnew.data.repository.futureSchedule

import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleCacheDataSource
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleRemoteDataSource
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleRoomDataSource
import com.example.flighttrackerappnew.domain.repository.FutureScheduleFlightRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class FutureScheduleRepositoryImpl(
    private val futureScheduleCacheDataSource: FutureScheduleCacheDataSource,
    private val futureScheduleRemoteDataSource: FutureScheduleRemoteDataSource,
    private val futureScheduleRoomDataSource: FutureScheduleRoomDataSource
) : FutureScheduleFlightRepository {

    override suspend fun getFutureScheduleFlightData(): Resource<List<FutureScheduleItem>> {
        return try {
            Resource.Success(getFromApi())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun getFromRoom(): List<FutureScheduleItem> {
        if (futureScheduleRoomDataSource.getFutureFlightFromRoom().isNotEmpty()) {
            futureScheduleCacheDataSource.saveFutureFlightToCache(futureScheduleRoomDataSource.getFutureFlightFromRoom())
            return futureScheduleRoomDataSource.getFutureFlightFromRoom()
        } else {
            return getFromApi()
        }
    }

    private suspend fun getFromApi(): List<FutureScheduleItem> {
        val flightData = futureScheduleRemoteDataSource.getFutureFlightData()
        futureScheduleRoomDataSource.saveFutureFlightToRoom(flightData)
        futureScheduleCacheDataSource.saveFutureFlightToCache(flightData)
        return flightData
    }
}