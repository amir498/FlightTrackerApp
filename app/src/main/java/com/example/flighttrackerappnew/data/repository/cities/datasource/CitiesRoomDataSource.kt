package com.example.flighttrackerappnew.data.repository.cities.datasource

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems

interface CitiesRoomDataSource {
    suspend fun getCitiesFromRoom(): List<CitiesDataItems>
    suspend fun saveCitiesToRoom(dataFromRemote: List<CitiesDataItems>)
}