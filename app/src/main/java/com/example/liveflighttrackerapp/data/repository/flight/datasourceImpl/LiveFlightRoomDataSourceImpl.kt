package com.example.liveflighttrackerapp.data.repository.flight.datasourceImpl

import com.example.liveflighttrackerapp.data.db.LiveFlightDao
import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.data.repository.flight.datasource.LiveFlightRoomDataSource

class LiveFlightRoomDataSourceImpl(private val liveFlightDao: LiveFlightDao):LiveFlightRoomDataSource {
    override suspend fun getLiveFlightFromRoom(): List<FlightDataItem> {
      return  liveFlightDao.getLiveFlightData()
    }

    override suspend fun saveLiveFlightToRoom(flightDataItems: List<FlightDataItem>) {
        liveFlightDao.insertLiveFlightData(flightDataItems)
    }
}