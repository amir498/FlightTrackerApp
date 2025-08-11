package com.example.liveflighttrackerapp.data.repository.nearby.datasourceImpl

import com.example.liveflighttrackerapp.data.api.NearbyService
import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems
import com.example.liveflighttrackerapp.data.repository.nearby.datasource.NearByAirPortsRemoteDataSource

class NearByAirPortsRemoteDataSourceImpl (private var nearByService: NearbyService):NearByAirPortsRemoteDataSource {
    override suspend fun getNearByData(): List<NearByAirportsDataItems> {
     return   nearByService.getNearby()
    }
}