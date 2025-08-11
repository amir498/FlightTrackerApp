package com.example.liveflighttrackerapp.data.repository.airports.datasource

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems

interface AirPortsCacheDataSource {
    suspend fun getAirportsCacheData(): ArrayList<AirportsDataItems>
    suspend fun saveAirportsToCache(staticAirLineItems: List<AirportsDataItems>)
}