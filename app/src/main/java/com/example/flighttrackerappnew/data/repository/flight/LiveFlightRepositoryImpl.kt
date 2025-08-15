package com.example.flighttrackerappnew.data.repository.flight

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightCacheDataSource
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightRemoteDataSource
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightRoomDataSource
import com.example.flighttrackerappnew.domain.repository.LiveFlightRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class LiveFlightRepositoryImpl(
    private val liveFlightCacheDataSource: LiveFlightCacheDataSource,
    private val liveFlightRemoteDataSource: LiveFlightRemoteDataSource,
    private val liveFlightRoomDataSource: LiveFlightRoomDataSource,
) : LiveFlightRepository {

    override suspend fun getLiveFlightData(): Resource<List<FlightDataItem>> {
        return try {
            val cached = liveFlightCacheDataSource.getLiveFlightFromCache()
            if (cached.isNotEmpty()) {
                Resource.Success(cached)
            } else {
                Resource.Success(getDataFromRemote())
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    private suspend fun getFromRoom(): List<FlightDataItem> {
        val dataFromRoom = liveFlightRoomDataSource.getLiveFlightFromRoom()
        return if (dataFromRoom.isNotEmpty()) {
            liveFlightCacheDataSource.saveLiveFlightToCache(dataFromRoom)
            dataFromRoom
        } else {
            getDataFromRemote()
        }
    }

    private suspend fun getDataFromRemote(): List<FlightDataItem> {
        val remote = liveFlightRemoteDataSource.getLiveFlightData()
        liveFlightCacheDataSource.saveLiveFlightToCache(remote)
        liveFlightRoomDataSource.saveLiveFlightToRoom(remote)
        return remote
    }
}