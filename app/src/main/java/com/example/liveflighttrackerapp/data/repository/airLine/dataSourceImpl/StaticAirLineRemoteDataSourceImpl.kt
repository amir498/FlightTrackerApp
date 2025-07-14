package com.example.liveflighttrackerapp.data.repository.airLine.dataSourceImpl

import com.example.liveflighttrackerapp.data.api.StaticAirLineService
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource

class StaticAirLineRemoteDataSourceImpl(
    private var staticAirLineService: StaticAirLineService
) : StaticAirLineRemoteDataSource {
    override suspend fun getStaticAirLineFromRemote(): List<StaticAirLineItems> {
       return staticAirLineService.getStaticAirLine()
    }
}