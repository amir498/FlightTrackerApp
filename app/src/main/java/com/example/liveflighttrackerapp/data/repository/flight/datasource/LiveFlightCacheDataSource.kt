package com.example.liveflighttrackerapp.data.repository.flight.datasource

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem

interface LiveFlightCacheDataSource {
    suspend fun getLiveFlightFromCache(): List<FlightDataItem>
    fun saveLiveFlightToCache(liveFlightData: List<FlightDataItem>)
}