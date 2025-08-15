package com.example.flighttrackerappnew.data.repository.airplane

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesCacheDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRemoteDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRoomDataSource
import com.example.flighttrackerappnew.domain.repository.AirPlanesRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class AirPlanesRepositoryImpl(
    private val airPlanesRemoteDataSource: AirPlanesRemoteDataSource,
    private val airPlanesRoomDataSource: AirPlanesRoomDataSource,
    private val airPlanesCacheDataSource: AirPlanesCacheDataSource
) : AirPlanesRepository {

    override suspend fun getAirPlaneData(): Resource<List<AirPlaneItems>> {
        val cacheDat = airPlanesCacheDataSource.getAirPlanesCacheData()

        return try {
            if (cacheDat.isNotEmpty()) {
                Resource.Success(cacheDat)
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