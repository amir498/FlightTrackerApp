package com.example.liveflighttrackerapp.data.repository.airLine.dataSourceImpl

import com.example.liveflighttrackerapp.data.db.StaticAirLineDao
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineRoomDataSource

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