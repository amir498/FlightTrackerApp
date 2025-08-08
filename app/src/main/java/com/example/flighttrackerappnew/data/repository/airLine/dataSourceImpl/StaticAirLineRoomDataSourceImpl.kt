package com.example.flighttrackerappnew.data.repository.airLine.dataSourceImpl

import com.example.flighttrackerappnew.data.db.StaticAirLineDao
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRoomDataSource

class StaticAirLineRoomDataSourceImpl(
    private val staticAirLineDao: StaticAirLineDao
):StaticAirLineRoomDataSource {
    override suspend fun getStaticAirLineFromRoom(): List<StaticAirLineItems> {
        return staticAirLineDao.getStaticAirLineData()
    }

    override suspend fun saveStaticAirLineToRoom(dataFromRemote: List<StaticAirLineItems>) {
        staticAirLineDao.insertStaticAirLineData(dataFromRemote)
    }
}