package com.example.flighttrackerappnew.data.repository.airLine.dataSourceImpl

import com.example.flighttrackerappnew.data.api.StaticAirLineService
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource

class StaticAirLineRemoteDataSourceImpl(
    private var staticAirLineService: StaticAirLineService
) : StaticAirLineRemoteDataSource {
    override suspend fun getStaticAirLineFromRemote(): List<StaticAirLineItems> {
       return staticAirLineService.getStaticAirLine()
    }
}