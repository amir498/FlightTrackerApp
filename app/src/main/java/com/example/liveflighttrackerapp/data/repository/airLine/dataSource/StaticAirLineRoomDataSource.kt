package com.example.liveflighttrackerapp.data.repository.airLine.dataSource

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems

interface StaticAirLineRoomDataSource {

    suspend fun getStaticAirLineFromRoom(): List<StaticAirLineItems>
    suspend fun saveStaticAirLineToRoom(dataFromRemote: List<StaticAirLineItems>)
}