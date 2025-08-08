package com.example.flighttrackerappnew.data.repository.airplane.datasource

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems

interface AirPlanesCacheDataSource {
    suspend fun getAirPlanesCacheData(): List<AirPlaneItems>
    suspend fun saveAirPlanesToCache(airPlaneItems: List<AirPlaneItems>)
}
