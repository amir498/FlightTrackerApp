package com.example.flighttrackerappnew.data.repository.nearby.datasource

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems

interface NearByAirPortsRemoteDataSource {
    suspend fun getNearByData(): List<NearByAirportsDataItems>
}