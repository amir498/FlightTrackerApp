package com.example.flighttrackerappnew.data.repository.nearby.datasourceImpl

import com.example.flighttrackerappnew.data.api.NearbyService
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

class NearByAirPortsRemoteDataSourceImpl (private var nearByService: NearbyService):NearByAirPortsRemoteDataSource {
    override suspend fun getNearByData(lat: Double,long: Double,distance: Int): List<NearByAirportsDataItems> {
        val apiKey = RemoteConfigManager.getString("api_key")
     return   nearByService.getNearby(lat,long,distance,apiKey)
    }
}