package com.example.flighttrackerappnew.data.repository.airLine

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineCacheDataSource
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRoomDataSource
import com.example.flighttrackerappnew.domain.repository.StaticAirLineRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class StaticAirLineRepositoryImpl(
    private val staticAirLineRemoteDataSource: StaticAirLineRemoteDataSource,
    private val staticAirLineRoomDataSource: StaticAirLineRoomDataSource,
    private val staticAirLineCacheDataSource: StaticAirLineCacheDataSource
) : StaticAirLineRepository {
    override suspend fun getStaticAirLineData(): Resource<List<StaticAirLineItems>> {
        val cacheDat = staticAirLineCacheDataSource.getStaticAirLineCacheData()
        return try {
            if (cacheDat.isNotEmpty()) {
                Resource.Success(cacheDat)
            } else {
                Resource.Success(getDataFromRemote())
            }
        } catch (e: HttpException) {
            Resource.Error("HTTP ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.localizedMessage}")
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