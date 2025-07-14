package com.example.liveflighttrackerapp.data.repository.nearby.datasource

import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems

interface NearByAirPortsRoomDataSource {
    suspend fun getNearByFromRoom(): List<NearByAirportsDataItems>
    suspend fun saveNearBYToRoom(nearByAirportsDataItems: List<NearByAirportsDataItems>)
}