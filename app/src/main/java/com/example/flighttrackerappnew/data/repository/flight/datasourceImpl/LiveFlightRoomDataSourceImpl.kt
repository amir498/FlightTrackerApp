package com.example.flighttrackerappnew.data.repository.flight.datasourceImpl

import com.example.flighttrackerappnew.data.db.LiveFlightDao
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.repository.flight.datasource.LiveFlightRoomDataSource

class LiveFlightRoomDataSourceImpl(private val liveFlightDao: LiveFlightDao):LiveFlightRoomDataSource {
    override suspend fun getLiveFlightFromRoom(): List<FlightDataItem> {
      return  liveFlightDao.getLiveFlightData()
    }

    override suspend fun saveLiveFlightToRoom(flightDataItems: List<FlightDataItem>) {
        liveFlightDao.insertLiveFlightData(flightDataItems)
    }
}