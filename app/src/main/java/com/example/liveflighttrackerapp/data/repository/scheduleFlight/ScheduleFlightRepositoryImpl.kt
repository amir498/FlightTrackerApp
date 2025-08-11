package com.example.liveflighttrackerapp.data.repository.scheduleFlight

import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightCacheDataSource
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.scheduleFlight.dataSource.ScheduleFlightRoomDataSource
import com.example.liveflighttrackerapp.domain.repository.ScheduleFlightRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class ScheduleFlightRepositoryImpl(
    private val scheduleFlightRemoteDataSource: ScheduleFlightRemoteDataSource,
    private val scheduleFlightRoomDataSource: ScheduleFlightRoomDataSource,
    private val scheduleFlightCacheDataSource: ScheduleFlightCacheDataSource
) : ScheduleFlightRepository {

    override suspend fun getScheduleFlightData(): Resource<List<SchedulesFlightsItems>> {
        return try {
            if (scheduleFlightCacheDataSource.getLiveFlightFromCache().isNotEmpty()) {
                Resource.Success(scheduleFlightCacheDataSource.getLiveFlightFromCache())
            } else {
                Resource.Success(getFromRoom())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun getFromRoom(): List<SchedulesFlightsItems> {
        if (scheduleFlightRoomDataSource.getLiveFlightFromRoom().isNotEmpty()) {
            scheduleFlightCacheDataSource.saveLiveFlightToCache(scheduleFlightRoomDataSource.getLiveFlightFromRoom())
            return scheduleFlightRoomDataSource.getLiveFlightFromRoom()
        } else {
            return getFromApi()
        }
    }

    private suspend fun getFromApi(): List<SchedulesFlightsItems> {
        val flightData = scheduleFlightRemoteDataSource.getLiveFlightData()
        scheduleFlightRoomDataSource.saveLiveFlightToRoom(flightData)
        scheduleFlightCacheDataSource.saveLiveFlightToCache(flightData)
        return flightData
    }
}