package com.example.flighttrackerappnew.data.repository.airports.datasource

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems

interface AirPortsCacheDataSource {
    suspend fun getAirportsCacheData(): ArrayList<AirportsDataItems>
    suspend fun saveAirportsToCache(staticAirLineItems: List<AirportsDataItems>)
}