package com.example.liveflighttrackerapp.data.repository.airplane.datasourceImpl

import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.repository.airplane.datasource.AirPlanesCacheDataSource

class AirPlanesCacheDataSourceImpl : AirPlanesCacheDataSource {
    private var airPlaneList = ArrayList<AirPlaneItems>()
    override suspend fun getAirPlanesCacheData(): List<AirPlaneItems> {
        return airPlaneList
    }

    override suspend fun saveAirPlanesToCache(airPlaneItems: List<AirPlaneItems>) {
        this.airPlaneList = ArrayList(airPlaneItems)
    }
}