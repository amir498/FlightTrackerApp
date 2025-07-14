package com.example.liveflighttrackerapp.data.repository.airplane.datasource

import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems

interface AirPlanesCacheDataSource {
    suspend fun getAirPlanesCacheData(): List<AirPlaneItems>
    suspend fun saveAirPlanesToCache(airPlaneItems: List<AirPlaneItems>)
}
