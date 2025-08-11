package com.example.liveflighttrackerapp.data.repository.flight.datasourceImpl

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.data.repository.flight.datasource.LiveFlightCacheDataSource

class LiveFlightCacheDataSourceImpl: LiveFlightCacheDataSource {
    private var liveFlightList = ArrayList<FlightDataItem>()

    override suspend fun getLiveFlightFromCache(): List<FlightDataItem> {
        return liveFlightList
    }

    override fun saveLiveFlightToCache(liveFlightData: List<FlightDataItem>) {
        this.liveFlightList.clear()
        this.liveFlightList = ArrayList(liveFlightData)
    }

}