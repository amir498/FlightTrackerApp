package com.example.flighttrackerappnew.data.repository.flightScheduleForSpecificIataNo

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleCacheDataSource
import com.example.flighttrackerappnew.data.repository.flightSchedule.dataSource.FlightScheduleRemoteDataSource
import com.example.flighttrackerappnew.data.repository.flightScheduleForSpecificIataNo.dataSource.FlightScheduleRemoteDataSourceForSpecificIataNumber
import com.example.flighttrackerappnew.domain.repository.FlightScheduleForSpecificIataNoRepository
import com.example.flighttrackerappnew.domain.repository.FlightScheduleRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import retrofit2.HttpException
import java.io.IOException

class FlightScheduleRepositoryForSpecificIataNoImpl(
    private val flightScheduleRemoteDataSourceForSpecificIataNumber: FlightScheduleRemoteDataSourceForSpecificIataNumber,
) : FlightScheduleForSpecificIataNoRepository {

    override suspend fun getScheduleFlightDataForSpecificIataNumber(iataNo: String): Resource<List<FlightSchedulesItems>> {
        return try {
            Resource.Success(flightScheduleRemoteDataSourceForSpecificIataNumber.getScheduleFlightDataForSpecificIataNumber(iataNo))
        } catch (e: HttpException) {
            Resource.Error("HTTP ${e.code()} ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error: ${e.localizedMessage}")
        }
    }
}