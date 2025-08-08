package com.example.flighttrackerappnew.data.repository.airports

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsCacheDataSource
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsRemoteDataSource
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsRoomDataSource
import com.example.flighttrackerappnew.domain.repository.AirPortsRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class AirPortsRepositoryImpl(
    private val airPortsRemoteDataSource: AirPortsRemoteDataSource,
    private val airPortsCacheDataSource: AirPortsCacheDataSource,
    private val airPortsRoomDataSource: AirPortsRoomDataSource
):AirPortsRepository {
    override suspend fun getAirportsData(): Resource<List<AirportsDataItems>> {
        val cacheDat = airPortsCacheDataSource.getAirportsCacheData()
        return if (cacheDat.isNotEmpty()) {
            Resource.Success(cacheDat)
        } else {
            Resource.Success(getDataFromRoom())
        }
    }

    private suspend fun getDataFromRoom(): List<AirportsDataItems> {
        val dataFromRoom = airPortsRoomDataSource.getAirportsFromRoom()
        return if (dataFromRoom.isNotEmpty()) {
            airPortsCacheDataSource.saveAirportsToCache(dataFromRoom)
            dataFromRoom
        } else {
            getDataFromRemote()
        }
    }

    private suspend fun getDataFromRemote(): List<AirportsDataItems> {
        val dataFromRemote = airPortsRemoteDataSource.getAirPortsFromRemote()
        airPortsRoomDataSource.saveAirportsToRoom(dataFromRemote)
        airPortsCacheDataSource.saveAirportsToCache(dataFromRemote)
        return dataFromRemote
    }
}