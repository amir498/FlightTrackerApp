package com.example.flighttrackerappnew.data.repository.airLine.dataSourceImpl

import com.example.flighttrackerappnew.data.api.StaticAirLineService
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

class StaticAirLineRemoteDataSourceImpl(
    private var staticAirLineService: StaticAirLineService
) : StaticAirLineRemoteDataSource {
    override suspend fun getStaticAirLineFromRemote(): List<StaticAirLineItems> {
        val apiKey = RemoteConfigManager.getString("api_key")
       return staticAirLineService.getStaticAirLine(apiKey)
    }
}