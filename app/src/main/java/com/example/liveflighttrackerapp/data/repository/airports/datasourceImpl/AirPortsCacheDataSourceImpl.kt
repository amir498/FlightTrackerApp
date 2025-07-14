package com.example.liveflighttrackerapp.data.repository.airports.datasourceImpl

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.repository.airports.datasource.AirPortsCacheDataSource

class AirPortsCacheDataSourceImpl : AirPortsCacheDataSource {
    private var airportsList = ArrayList<AirportsDataItems>()
    override suspend fun getAirportsCacheData(): ArrayList<AirportsDataItems> {
        return airportsList
    }

    override suspend fun saveAirportsToCache(staticAirLineItems: List<AirportsDataItems>) {
        this.airportsList = staticAirLineItems as ArrayList<AirportsDataItems>
    }
}