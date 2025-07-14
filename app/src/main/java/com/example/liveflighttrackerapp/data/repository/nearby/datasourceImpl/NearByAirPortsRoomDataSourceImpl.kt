package com.example.liveflighttrackerapp.data.repository.nearby.datasourceImpl

import com.example.liveflighttrackerapp.data.db.NearByDao
import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems
import com.example.liveflighttrackerapp.data.repository.nearby.datasource.NearByAirPortsRoomDataSource

class NearByAirPortsRoomDataSourceImpl(private val nearByDao: NearByDao) : NearByAirPortsRoomDataSource {
    override suspend fun getNearByFromRoom(): List<NearByAirportsDataItems> {
        return nearByDao.getNearByData()
    }

    override suspend fun saveNearBYToRoom(nearByAirportsDataItems: List<NearByAirportsDataItems>) {
        nearByDao.insertNearBy(nearByAirportsDataItems)
    }
}