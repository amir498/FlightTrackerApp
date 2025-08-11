package com.example.liveflighttrackerapp.data.repository.airplane.datasourceImpl

import com.example.liveflighttrackerapp.data.api.AirPlanesService
import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.repository.airplane.datasource.AirPlanesRemoteDataSource

class AirPlanesRemoteDataSourceImpl(
    private val airPlanesService: AirPlanesService
):AirPlanesRemoteDataSource {
    override suspend fun getStaticAirPlaneFromRemote(): List<AirPlaneItems> {
        return airPlanesService.getAirPlanesData()
    }
}