package com.example.flighttrackerappnew.data.repository.airports.datasourceImpl

import com.example.flighttrackerappnew.data.api.AirportsService
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

class AirPortsRemoteDataSourceImpl(private val airPortsService: AirportsService):AirPortsRemoteDataSource {
    override suspend fun getAirPortsFromRemote(): List<AirportsDataItems> {
        val apiKey = RemoteConfigManager.getString("api_key")
        return airPortsService.getAirportsLine(apiKey)
    }
}