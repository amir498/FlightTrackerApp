package com.example.liveflighttrackerapp.data.repository.airports.datasourceImpl

import com.example.liveflighttrackerapp.data.api.AirportsService
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.repository.airports.datasource.AirPortsRemoteDataSource

class AirPortsRemoteDataSourceImpl(private val airPortsService: AirportsService):AirPortsRemoteDataSource {
    override suspend fun getAirPortsFromRemote(): List<AirportsDataItems> {
        return airPortsService.getAirportsLine()
    }
}