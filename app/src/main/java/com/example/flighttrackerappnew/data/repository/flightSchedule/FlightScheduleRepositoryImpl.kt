package com.example.flighttrackerappnew.data.repository.flightSchedule

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleCacheDataSource
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRemoteDataSource
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRoomDataSource
import com.example.flighttrackerappnew.domain.repository.FlightScheduleRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class FlightScheduleRepositoryImpl(
    private val flightScheduleRemoteDataSource: FlightScheduleRemoteDataSource,
    private val flightScheduleRoomDataSource: FlightScheduleRoomDataSource,
    private val flightScheduleCacheDataSource: FlightScheduleCacheDataSource
) : FlightScheduleRepository {

    override suspend fun getScheduleFlightData(): Resource<List<FlightSchedulesItems>> {
        return try {
            if (flightScheduleCacheDataSource.getLiveFlightFromCache().isNotEmpty()) {
                Resource.Success(flightScheduleCacheDataSource.getLiveFlightFromCache())
            } else {
                Resource.Success(getFromRoom())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun getFromRoom(): List<FlightSchedulesItems> {
        if (flightScheduleRoomDataSource.getLiveFlightFromRoom().isNotEmpty()) {
            flightScheduleCacheDataSource.saveLiveFlightToCache(flightScheduleRoomDataSource.getLiveFlightFromRoom())
            return flightScheduleRoomDataSource.getLiveFlightFromRoom()
        } else {
            return getFromApi()
        }
    }

    private suspend fun getFromApi(): List<FlightSchedulesItems> {
        val flightData = flightScheduleRemoteDataSource.getLiveFlightData()
        flightScheduleRoomDataSource.saveLiveFlightToRoom(flightData)
        flightScheduleCacheDataSource.saveLiveFlightToCache(flightData)
        return flightData
    }
}