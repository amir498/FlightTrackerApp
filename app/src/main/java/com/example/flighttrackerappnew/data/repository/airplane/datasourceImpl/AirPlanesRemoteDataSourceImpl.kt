package com.example.flighttrackerappnew.data.repository.airplane.datasourceImpl

import com.example.flighttrackerappnew.data.api.AirPlanesService
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

class AirPlanesRemoteDataSourceImpl(
    private val airPlanesService: AirPlanesService
):AirPlanesRemoteDataSource {
    override suspend fun getStaticAirPlaneFromRemote(): List<AirPlaneItems> {
        val apiKey = RemoteConfigManager.getString("api_key")
        return airPlanesService.getAirPlanesData(apiKey)
    }
}