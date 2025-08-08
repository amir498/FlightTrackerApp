package com.example.flighttrackerappnew.data.repository.airplane

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesCacheDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRemoteDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRoomDataSource
import com.example.flighttrackerappnew.domain.repository.AirPlanesRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class AirPlanesRepositoryImpl(
    private val airPlanesRemoteDataSource: AirPlanesRemoteDataSource,
    private val airPlanesRoomDataSource: AirPlanesRoomDataSource,
    private val airPlanesCacheDataSource: AirPlanesCacheDataSource
) : AirPlanesRepository {

    override suspend fun getAirPlaneData(): Resource<List<AirPlaneItems>> {
        val cacheDat = airPlanesCacheDataSource.getAirPlanesCacheData()
        return if (cacheDat.isNotEmpty()) {
            Resource.Success(cacheDat)
        } else {
            Resource.Success(getDataFromRoom())
        }
    }

    private suspend fun getDataFromRoom(): List<AirPlaneItems> {
        val dataFromRoom = airPlanesRoomDataSource.getAirPlanesFromRoom()
        return if (dataFromRoom.isNotEmpty()) {
            airPlanesCacheDataSource.saveAirPlanesToCache(dataFromRoom)
            dataFromRoom
        } else {
            getDataFromRemote()
        }
    }

    private suspend fun getDataFromRemote(): List<AirPlaneItems> {
        val dataFromRemote = airPlanesRemoteDataSource.getStaticAirPlaneFromRemote()
        airPlanesRoomDataSource.saveAirPlanesToRoom(dataFromRemote)
        airPlanesCacheDataSource.saveAirPlanesToCache(dataFromRemote)
        return dataFromRemote
    }
}