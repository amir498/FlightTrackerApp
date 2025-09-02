package com.example.flighttrackerappnew.data.repository.cities.datasourceImpl

import com.example.flighttrackerappnew.data.api.CitiesService
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.repository.cities.datasource.CitiesRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

class CitiesRemoteDataSourceImpl (private var citiesService: CitiesService):CitiesRemoteDataSource {
    override suspend fun getCitiesFromRemote(): List<CitiesDataItems> {
        val apiKey = RemoteConfigManager.getString("api_key")
        return citiesService.getCities(apiKey)
    }
}