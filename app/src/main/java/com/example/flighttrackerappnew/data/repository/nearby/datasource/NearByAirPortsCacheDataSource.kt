package com.example.flighttrackerappnew.data.repository.nearby.datasource

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems

interface NearByAirPortsCacheDataSource {
    suspend fun getNearByFromCache(): List<NearByAirportsDataItems>
    fun saveNearByToCache(nearByData: List<NearByAirportsDataItems>)
}