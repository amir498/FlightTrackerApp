package com.example.liveflighttrackerapp.data.repository.airLine.dataSourceImpl

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineCacheDataSource

class StaticAirLineCacheDataSourceImpl : StaticAirLineCacheDataSource {
    private var staticAirLineItemsList = ArrayList<StaticAirLineItems>()

    override suspend fun getStaticAirLineCacheData(): List<StaticAirLineItems> {
        return staticAirLineItemsList
    }

    override suspend fun saveStaticAirLineToCache(staticAirLineItems: List<StaticAirLineItems>) {
        this.staticAirLineItemsList = staticAirLineItems as ArrayList<StaticAirLineItems>
    }


}