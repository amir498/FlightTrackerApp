package com.example.liveflighttrackerapp.data.repository.nearby.datasource

import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems

interface NearByAirPortsRemoteDataSource {
    suspend fun getNearByData(): List<NearByAirportsDataItems>
}