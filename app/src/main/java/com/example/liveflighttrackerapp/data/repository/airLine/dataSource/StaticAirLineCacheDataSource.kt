package com.example.liveflighttrackerapp.data.repository.airLine.dataSource

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems

interface StaticAirLineCacheDataSource {
    suspend fun getStaticAirLineCacheData(): List<StaticAirLineItems>
    suspend fun saveStaticAirLineToCache(staticAirLineItems: List<StaticAirLineItems>)
}
