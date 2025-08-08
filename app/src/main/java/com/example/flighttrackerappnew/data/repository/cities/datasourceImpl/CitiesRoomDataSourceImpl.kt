package com.example.flighttrackerappnew.data.repository.cities.datasourceImpl

import com.example.flighttrackerappnew.data.db.CitiesDao
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRoomDataSource

class CitiesRoomDataSourceImpl(private val citiesDao: CitiesDao) : CitiesRoomDataSource {

    override suspend fun getCitiesFromRoom(): List<CitiesDataItems> {
        return citiesDao.getCitiesData()
    }

    override suspend fun saveCitiesToRoom(dataFromRemote: List<CitiesDataItems>) {
        citiesDao.insertCitiesData(dataFromRemote)
    }
}