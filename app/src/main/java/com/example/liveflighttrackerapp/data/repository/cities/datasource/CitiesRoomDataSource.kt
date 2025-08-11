package com.example.liveflighttrackerapp.data.repository.cities.datasource

import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems

interface CitiesRoomDataSource {
    suspend fun getCitiesFromRoom(): List<CitiesDataItems>
    suspend fun saveCitiesToRoom(dataFromRemote: List<CitiesDataItems>)
}