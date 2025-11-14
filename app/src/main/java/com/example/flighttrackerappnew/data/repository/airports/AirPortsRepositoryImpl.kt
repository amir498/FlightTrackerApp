package com.example.flighttrackerappnew.data.repository.airports

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsCacheDataSource
import com.example.flighttrackerappnew.data.repository.airports.datasource.AirPortsRemoteDataSource
import com.example.flighttrackerappnew.domain.repository.AirPortsRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class AirPortsRepositoryImpl(
    private val airPortsRemoteDataSource: AirPortsRemoteDataSource,
    private val airPortsCacheDataSource: AirPortsCacheDataSource,
) : AirPortsRepository {

    override suspend fun getAirportsData(): Resource<List<AirportsDataItems>> {
        return try {
            val cacheData = airPortsCacheDataSource.getAirportsCacheData()
            if (cacheData.isNotEmpty()) {
                return Resource.Success(cacheData)
            }
            val remoteData = airPortsRemoteDataSource.getAirPortsFromRemote()
            airPortsCacheDataSource.saveAirportsToCache(remoteData)
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