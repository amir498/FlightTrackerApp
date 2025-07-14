package com.example.liveflighttrackerapp.data.repository.cities.datasourceImpl

import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import com.example.liveflighttrackerapp.data.repository.cities.datasource.CitiesCacheDataSource

class CitiesCacheDataSourceImpl:CitiesCacheDataSource {
    private var citiesList = ArrayList<CitiesDataItems>()

    override suspend fun getCitiesCacheData(): ArrayList<CitiesDataItems> {
        return citiesList
    }

    override suspend fun saveCitiesToCache(staticAirLineItems: List<CitiesDataItems>) {
        this.citiesList = staticAirLineItems as ArrayList<CitiesDataItems>
    }
}