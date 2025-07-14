package com.example.liveflighttrackerapp.data.repository.cities.datasource

import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems

interface CitiesRemoteDataSource {
    suspend fun getCitiesFromRemote(): List<CitiesDataItems>
}