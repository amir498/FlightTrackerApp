package com.example.flighttrackerappnew.data.repository.airLine.dataSource

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems

interface StaticAirLineCacheDataSource {
    suspend fun getStaticAirLineCacheData(): List<StaticAirLineItems>
    suspend fun saveStaticAirLineToCache(staticAirLineItems: List<StaticAirLineItems>)
}
