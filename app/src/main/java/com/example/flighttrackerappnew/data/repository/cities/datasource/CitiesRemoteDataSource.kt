package com.example.flighttrackerappnew.data.repository.cities.datasource

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems

interface CitiesRemoteDataSource {
    suspend fun getCitiesFromRemote(): List<CitiesDataItems>
}