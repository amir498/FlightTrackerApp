package com.example.flighttrackerappnew.data.repository.nearby

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsCacheDataSource
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRemoteDataSource
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRoomDataSource
import com.example.flighttrackerappnew.domain.repository.NearByAirPortsRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class NearByAirportsAirPortsRepositoryImpl(
    private val nearByAirPortsRemoteDataSource: NearByAirPortsRemoteDataSource,
    private val nearByAirPortsRoomDataSource: NearByAirPortsRoomDataSource,
    private val nearByAirPortsCacheDataSource: NearByAirPortsCacheDataSource
) : NearByAirPortsRepository {

    override suspend fun getNearbyData(): Resource<List<NearByAirportsDataItems>> {
        return try {
            val cached = nearByAirPortsCacheDataSource.getNearByFromCache()
            if (cached.isNotEmpty()) {
                Resource.Success(cached)
            } else {
                Resource.Success(getFromRoom())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun getFromRoom(): List<NearByAirportsDataItems> {
        val dataFromRoom = nearByAirPortsRoomDataSource.getNearByFromRoom()
        return if (dataFromRoom.isNotEmpty()) {
            nearByAirPortsCacheDataSource.saveNearByToCache(dataFromRoom)
            dataFromRoom
        } else {
            getDataFromRemote()
        }
    }

    private suspend fun getDataFromRemote(): List<NearByAirportsDataItems> {
        val remote = nearByAirPortsRemoteDataSource.getNearByData()
        nearByAirPortsCacheDataSource.saveNearByToCache(remote)
        nearByAirPortsRoomDataSource.saveNearBYToRoom(remote)
        return remote
    }
}