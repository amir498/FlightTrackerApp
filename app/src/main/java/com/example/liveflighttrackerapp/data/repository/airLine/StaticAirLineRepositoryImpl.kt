package com.example.liveflighttrackerapp.data.repository.airLine

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineCacheDataSource
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource
import com.example.liveflighttrackerapp.data.repository.airLine.dataSource.StaticAirLineRoomDataSource
import com.example.liveflighttrackerapp.domain.repository.StaticAirLineRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class StaticAirLineRepositoryImpl(
    private val staticAirLineRemoteDataSource: StaticAirLineRemoteDataSource,
    private val staticAirLineRoomDataSource: StaticAirLineRoomDataSource,
    private val staticAirLineCacheDataSource: StaticAirLineCacheDataSource
) : StaticAirLineRepository {
    override suspend fun getStaticAirLineData(): Resource<List<StaticAirLineItems>> {
        val cacheDat = staticAirLineCacheDataSource.getStaticAirLineCacheData()
        return if (cacheDat.isNotEmpty()) {
            Resource.Success(cacheDat)
        } else {
            Resource.Success(getDataFromRoom())
        }
    }

    private suspend fun getDataFromRoom(): List<StaticAirLineItems> {
        val dataFromRoom = staticAirLineRoomDataSource.getStaticAirLineFromRoom()
        return if (dataFromRoom.isNotEmpty()) {
            staticAirLineCacheDataSource.saveStaticAirLineToCache(dataFromRoom)
            dataFromRoom
        } else {
            getDataFromRemote()
        }
    }

    private suspend fun getDataFromRemote(): List<StaticAirLineItems> {
        val dataFromRemote = staticAirLineRemoteDataSource.getStaticAirLineFromRemote()
        staticAirLineRoomDataSource.saveStaticAirLineToRoom(dataFromRemote)
        staticAirLineCacheDataSource.saveStaticAirLineToCache(dataFromRemote)
        return dataFromRemote
    }
}