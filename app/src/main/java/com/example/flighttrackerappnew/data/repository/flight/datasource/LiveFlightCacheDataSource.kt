package com.example.flighttrackerappnew.data.repository.flight.datasource

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem

interface LiveFlightCacheDataSource {
    suspend fun getLiveFlightFromCache(): List<FlightDataItem>
    fun saveLiveFlightToCache(liveFlightData: List<FlightDataItem>)
}