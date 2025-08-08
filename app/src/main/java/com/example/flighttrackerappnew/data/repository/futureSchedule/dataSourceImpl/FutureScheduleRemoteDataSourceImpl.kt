package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl

import com.example.flighttrackerappnew.data.api.FutureScheduleFlightService
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleRemoteDataSource
import com.example.flighttrackerappnew.presentation.utils.airportCode
import com.example.flighttrackerappnew.presentation.utils.flightType
import com.example.flighttrackerappnew.presentation.utils.startDate

class FutureScheduleRemoteDataSourceImpl(
    private val futureScheduleFlightService: FutureScheduleFlightService
) : FutureScheduleRemoteDataSource {
    override suspend fun getFutureFlightData(): List<FutureScheduleItem> {
        return futureScheduleFlightService.getSchedulesFlight(
            type = flightType,
            iataCode = airportCode,
            date = startDate
        )
    }
}