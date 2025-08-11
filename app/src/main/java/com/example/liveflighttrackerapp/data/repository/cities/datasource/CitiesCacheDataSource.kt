package com.example.liveflighttrackerapp.data.repository.cities.datasource

import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems

interface CitiesCacheDataSource {
    suspend fun getCitiesCacheData(): ArrayList<CitiesDataItems>
    suspend fun saveCitiesToCache(staticAirLineItems: List<CitiesDataItems>)
}