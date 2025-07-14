package com.example.liveflighttrackerapp.data.repository.airplane.datasource

import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems

interface AirPlanesRoomDataSource {

    suspend fun getAirPlanesFromRoom(): List<AirPlaneItems>
    suspend fun saveAirPlanesToRoom(dataFromRemote: List<AirPlaneItems>)
}