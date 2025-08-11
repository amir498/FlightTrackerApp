package com.example.liveflighttrackerapp.data.repository.cities.datasourceImpl

import com.example.liveflighttrackerapp.data.api.CitiesService
import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import com.example.liveflighttrackerapp.data.repository.cities.datasource.CitiesRemoteDataSource

class CitiesRemoteDataSourceImpl (private var citiesService: CitiesService):CitiesRemoteDataSource {
    override suspend fun getCitiesFromRemote(): List<CitiesDataItems> {
        return citiesService.getCities()
    }
}