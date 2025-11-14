package com.example.flighttrackerappnew.data.repository.airLine

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineCacheDataSource
import com.example.flighttrackerappnew.data.repository.airLine.dataSource.StaticAirLineRemoteDataSource
import com.example.flighttrackerappnew.domain.repository.StaticAirLineRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class StaticAirLineRepositoryImpl(
    private val staticAirLineRemoteDataSource: StaticAirLineRemoteDataSource,
    private val staticAirLineCacheDataSource: StaticAirLineCacheDataSource
) : StaticAirLineRepository {

    override suspend fun getStaticAirLineData(): Resource<List<StaticAirLineItems>> {
        return try {
            val cacheData = staticAirLineCacheDataSource.getStaticAirLineCacheData()
            if (cacheData.isNotEmpty()) {
                return Resource.Success(cacheData)
            }
            val remoteData = staticAirLineRemoteDataSource.getStaticAirLineFromRemote()
            staticAirLineCacheDataSource.saveStaticAirLineToCache(remoteData)
            Resource.Success(remoteData)

        } catch (e: HttpException) {
            Resource.Error("HTTP ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.localizedMessage}")
        }
    }
}