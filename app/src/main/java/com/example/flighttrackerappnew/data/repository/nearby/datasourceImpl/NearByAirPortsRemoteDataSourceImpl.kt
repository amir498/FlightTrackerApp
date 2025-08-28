package com.example.flighttrackerappnew.data.repository.nearby.datasourceImpl

import com.example.flighttrackerappnew.data.api.NearbyService
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRemoteDataSource

class NearByAirPortsRemoteDataSourceImpl (private var nearByService: NearbyService):NearByAirPortsRemoteDataSource {
    override suspend fun getNearByData(lat: Double,long: Double,distance: Int): List<NearByAirportsDataItems> {
     return   nearByService.getNearby(lat,long,distance)
    }
}