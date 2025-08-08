package com.example.flighttrackerappnew.data.repository.airLine.dataSource

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems

interface StaticAirLineRoomDataSource {

    suspend fun getStaticAirLineFromRoom(): List<StaticAirLineItems>
    suspend fun saveStaticAirLineToRoom(dataFromRemote: List<StaticAirLineItems>)
}