package com.example.liveflighttrackerapp.data.repository.airplane.datasourceImpl

import com.example.liveflighttrackerapp.data.db.AirPlaneDao
import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.repository.airplane.datasource.AirPlanesRoomDataSource

class AirPlanesRoomDataSourceImpl(
    private val airPlaneDao: AirPlaneDao
) : AirPlanesRoomDataSource {
    override suspend fun getAirPlanesFromRoom(): List<AirPlaneItems> {
        return airPlaneDao.getAirPlaneData()
    }

    override suspend fun saveAirPlanesToRoom(dataFromRemote: List<AirPlaneItems>) {
        airPlaneDao.insertAirPlaneData(dataFromRemote)
    }
}