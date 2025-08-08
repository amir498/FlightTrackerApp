package com.example.flighttrackerappnew.data.repository.airplane.datasourceImpl

import com.example.flighttrackerappnew.data.db.AirPlaneDao
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.repository.airplane.datasource.AirPlanesRoomDataSource

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