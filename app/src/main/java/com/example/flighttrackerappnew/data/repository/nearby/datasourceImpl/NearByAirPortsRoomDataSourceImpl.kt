package com.example.flighttrackerappnew.data.repository.nearby.datasourceImpl

import com.example.flighttrackerappnew.data.db.NearByDao
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.data.repository.nearby.datasource.NearByAirPortsRoomDataSource

class NearByAirPortsRoomDataSourceImpl(private val nearByDao: NearByDao) : NearByAirPortsRoomDataSource {
    override suspend fun getNearByFromRoom(): List<NearByAirportsDataItems> {
        return nearByDao.getNearByData()
    }

    override suspend fun saveNearBYToRoom(nearByAirportsDataItems: List<NearByAirportsDataItems>) {
        nearByDao.insertNearBy(nearByAirportsDataItems)
    }
}