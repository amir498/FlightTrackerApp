package com.example.flighttrackerappnew.data.repository.airLine.dataSourceImpl

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineCacheDataSource

class StaticAirLineCacheDataSourceImpl : StaticAirLineCacheDataSource {
    private var staticAirLineItemsList = ArrayList<StaticAirLineItems>()

    override suspend fun getStaticAirLineCacheData(): List<StaticAirLineItems> {
        return staticAirLineItemsList
    }

    override suspend fun saveStaticAirLineToCache(staticAirLineItems: List<StaticAirLineItems>) {
        this.staticAirLineItemsList = staticAirLineItems as ArrayList<StaticAirLineItems>
    }


}