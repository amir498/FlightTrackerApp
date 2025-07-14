package com.example.liveflighttrackerapp.data.repository.airports.datasourceImpl

import com.example.liveflighttrackerapp.data.db.AirportsDao
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.repository.airports.datasource.AirPortsRoomDataSource

class AirPortsRoomDataSourceImpl(private val airportsDao: AirportsDao):AirPortsRoomDataSource {
    override suspend fun getAirportsFromRoom(): List<AirportsDataItems> {
      return  airportsDao.getAirportsData()
    }

    override suspend fun saveAirportsToRoom(dataFromRemote: List<AirportsDataItems>) {
        airportsDao.insertAirportsData(dataFromRemote)
    }
}