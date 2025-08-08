package com.example.flighttrackerappnew.data.repository.nearby.datasource

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems

interface NearByAirPortsRoomDataSource {
    suspend fun getNearByFromRoom(): List<NearByAirportsDataItems>
    suspend fun saveNearBYToRoom(nearByAirportsDataItems: List<NearByAirportsDataItems>)
}