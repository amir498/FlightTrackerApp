package com.example.flighttrackerappnew.data.repository.airplane.datasourceImpl

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesCacheDataSource

class AirPlanesCacheDataSourceImpl : AirPlanesCacheDataSource {
    private var airPlaneList = ArrayList<AirPlaneItems>()
    override suspend fun getAirPlanesCacheData(): List<AirPlaneItems> {
        return airPlaneList
    }

    override suspend fun saveAirPlanesToCache(airPlaneItems: List<AirPlaneItems>) {
        this.airPlaneList = ArrayList(airPlaneItems)
    }
}