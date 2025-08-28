package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl

import android.util.Log
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
        Log.d("ksjahnsaTAG", "flightType:$flightType ")
        Log.d("ksjahnsaTAG", "airportCode:$airportCode ")
        Log.d("ksjahnsaTAG", "startDate:$startDate ")
        return futureScheduleFlightService.getSchedulesFlight(
            type = flightType,
            iataCode = airportCode.uppercase(),
            date = startDate
        )
    }
}