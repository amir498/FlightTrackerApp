package com.example.flighttrackerappnew.data.repository.nearby

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsCacheDataSource
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRemoteDataSource
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRoomDataSource
import com.example.flighttrackerappnew.domain.repository.NearByAirPortsRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class NearByAirportsAirPortsRepositoryImpl(
    private val nearByAirPortsRemoteDataSource: NearByAirPortsRemoteDataSource,
    private val nearByAirPortsRoomDataSource: NearByAirPortsRoomDataSource,
    private val nearByAirPortsCacheDataSource: NearByAirPortsCacheDataSource
) : NearByAirPortsRepository {

    override suspend fun getNearbyData(
        lat: Double,
        long: Double,
        distance: Int
    ): Resource<List<NearByAirportsDataItems>> {
        return try {
            val cached = nearByAirPortsCacheDataSource.getNearByFromCache()
            if (cached.isNotEmpty()) {
                Resource.Success(cached)
            } else {
                Resource.Success(getDataFromRemote(lat, long, distance))
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.localizedMessage}")
        }
    }

    private suspend fun getFromRoom(
        lat: Double,
        long: Double,
        distance: Int
    ): List<NearByAirportsDataItems> {
        val dataFromRoom = nearByAirPortsRoomDataSource.getNearByFromRoom()
        return if (dataFromRoom.isNotEmpty()) {
            nearByAirPortsCacheDataSource.saveNearByToCache(dataFromRoom)
            dataFromRoom
        } else {
            getDataFromRemote(lat, long, distance)
        }
    }

    private suspend fun getDataFromRemote(
        lat: Double,
        long: Double,
        distance: Int
    ): List<NearByAirportsDataItems> {
        val remote = nearByAirPortsRemoteDataSource.getNearByData(lat, long, distance)
        nearByAirPortsCacheDataSource.saveNearByToCache(remote)
        nearByAirPortsRoomDataSource.saveNearBYToRoom(remote)
        return remote
    }
}