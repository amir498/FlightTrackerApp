package com.example.liveflighttrackerapp.data.repository.nearby.datasource

import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems

interface NearByAirPortsCacheDataSource {
    suspend fun getNearByFromCache(): List<NearByAirportsDataItems>
    fun saveNearByToCache(nearByData: List<NearByAirportsDataItems>)
}