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
        val cacheDat = citiesCacheDataSource.getCitiesCacheData()
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

    private suspend fun getDataFromRoom(): List<CitiesDataItems> {
        val dataFromRoom = citiesRoomDataSource.getCitiesFromRoom()
        return if (dataFromRoom.isNotEmpty()) {
            citiesCacheDataSource.saveCitiesToCache(dataFromRoom)
            dataFromRoom
        } else {
            getDataFromRemote()
        }
    }

    private suspend fun getDataFromRemote(): List<CitiesDataItems> {
        val dataFromRemote = citiesRemoteDataSource.getCitiesFromRemote()
        citiesRoomDataSource.saveCitiesToRoom(dataFromRemote)
        citiesCacheDataSource.saveCitiesToCache(dataFromRemote)
        return dataFromRemote
    }
}