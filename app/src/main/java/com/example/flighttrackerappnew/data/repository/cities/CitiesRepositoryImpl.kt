package com.example.flighttrackerappnew.data.repository.cities

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesCacheDataSource
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRemoteDataSource
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRoomDataSource
import com.example.flighttrackerappnew.domain.repository.CitiesRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class CitiesRepositoryImpl(
    private val citiesRemoteDataSource: CitiesRemoteDataSource,
    private val citiesRoomDataSource: CitiesRoomDataSource,
    private val citiesCacheDataSource: CitiesCacheDataSource
) : CitiesRepository {

    override suspend fun getCitiesData(): Resource<List<CitiesDataItems>> {
        return try {
            val cacheData = citiesCacheDataSource.getCitiesCacheData()
            if (cacheData.isNotEmpty()) {
                return Resource.Success(cacheData)
            }
            val roomData = citiesRoomDataSource.getCitiesFromRoom()
            if (roomData.isNotEmpty()) {
                citiesCacheDataSource.saveCitiesToCache(roomData)
                return Resource.Success(roomData)
            }
            val remoteData = citiesRemoteDataSource.getCitiesFromRemote()
            citiesRoomDataSource.saveCitiesToRoom(remoteData)
            citiesCacheDataSource.saveCitiesToCache(remoteData)
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