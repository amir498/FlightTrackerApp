package com.example.liveflighttrackerapp.data.repository.nearby.datasourceImpl

import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems
import com.example.liveflighttrackerapp.data.repository.nearby.datasource.NearByAirPortsCacheDataSource

class NearByAirPortsCacheDataSourceImpl : NearByAirPortsCacheDataSource {
    private var nearByAirportsDataItems = ArrayList<NearByAirportsDataItems>()
    override suspend fun getNearByFromCache(): List<NearByAirportsDataItems> {
        return nearByAirportsDataItems
    }

    override fun saveNearByToCache(nearByData: List<NearByAirportsDataItems>) {
        this.nearByAirportsDataItems = ArrayList(nearByData)
    }
}