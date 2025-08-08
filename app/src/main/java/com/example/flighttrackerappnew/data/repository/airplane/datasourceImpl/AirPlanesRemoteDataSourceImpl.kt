package com.example.flighttrackerappnew.data.repository.airplane.datasourceImpl

import com.example.flighttrackerappnew.data.api.AirPlanesService
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRemoteDataSource

class AirPlanesRemoteDataSourceImpl(
    private val airPlanesService: AirPlanesService
):AirPlanesRemoteDataSource {
    override suspend fun getStaticAirPlaneFromRemote(): List<AirPlaneItems> {
        return airPlanesService.getAirPlanesData()
    }
}