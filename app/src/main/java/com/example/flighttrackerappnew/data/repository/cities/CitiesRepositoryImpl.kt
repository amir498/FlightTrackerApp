package com.example.flighttrackerappnew.data.repository.cities

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesCacheDataSource
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRemoteDataSource
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRoomDataSource
import com.example.flighttrackerappnew.domain.repository.CitiesRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class CitiesRepositoryImpl(
    private val citiesRemoteDataSource: CitiesRemoteDataSource,
    private val citiesRoomDataSource: CitiesRoomDataSource,
    private val citiesCacheDataSource: CitiesCacheDataSource
) : CitiesRepository {

    override suspend fun getCitiesData(): Resource<List<CitiesDataItems>> {
        val cacheDat = citiesCacheDataSource.getCitiesCacheData()
        return if (cacheDat.isNotEmpty()) {
            Resource.Success(cacheDat)
        } else {
            Resource.Success(getDataFromRemote())
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