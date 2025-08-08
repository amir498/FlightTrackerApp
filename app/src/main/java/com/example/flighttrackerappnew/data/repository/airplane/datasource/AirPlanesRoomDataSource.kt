package com.example.flighttrackerappnew.data.repository.airplane.datasource

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems

interface AirPlanesRoomDataSource {

    suspend fun getAirPlanesFromRoom(): List<AirPlaneItems>
    suspend fun saveAirPlanesToRoom(dataFromRemote: List<AirPlaneItems>)
}