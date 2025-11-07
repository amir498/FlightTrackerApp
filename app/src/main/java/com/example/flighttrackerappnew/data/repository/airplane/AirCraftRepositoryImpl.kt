package com.example.flighttrackerappnew.data.repository.airplane

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesCacheDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRemoteDataSource
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRoomDataSource
import com.example.flighttrackerappnew.domain.repository.AirCraftRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class AirCraftRepositoryImpl(
    private val airPlanesRemoteDataSource: AirPlanesRemoteDataSource,
    private val airPlanesRoomDataSource: AirPlanesRoomDataSource,
    private val airPlanesCacheDataSource: AirPlanesCacheDataSource
) : AirCraftRepository {

    override suspend fun getAirCraftData(): Resource<List<AirPlaneItems>> {
        return try {
            val cacheData = airPlanesCacheDataSource.getAirPlanesCacheData()
            if (cacheData.isNotEmpty()) {
                return Resource.Success(cacheData)
            }
//            val roomData = airPlanesRoomDataSource.getAirPlanesFromRoom()
//            if (roomData.isNotEmpty()) {
//                airPlanesCacheDataSource.saveAirPlanesToCache(roomData)
//                return Resource.Success(roomData)
//            }
            val remoteData = airPlanesRemoteDataSource.getStaticAirPlaneFromRemote()
//            airPlanesRoomDataSource.saveAirPlanesToRoom(remoteData)
            airPlanesCacheDataSource.saveAirPlanesToCache(remoteData)
            Resource.Success(remoteData)

        } catch (e: HttpException) {
            Resource.Error("HTTP ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.localizedMessage}")
        }
    }
}