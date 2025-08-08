package com.example.flighttrackerappnew.data.repository.cities.datasource

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems

interface CitiesCacheDataSource {
    suspend fun getCitiesCacheData(): ArrayList<CitiesDataItems>
    suspend fun saveCitiesToCache(staticAirLineItems: List<CitiesDataItems>)
}