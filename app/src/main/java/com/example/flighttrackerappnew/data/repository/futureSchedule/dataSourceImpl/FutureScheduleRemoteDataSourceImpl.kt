package com.example.flighttrackerappnew.data.repository.futureSchedule.dataSourceImpl

import android.util.Log
import com.example.flighttrackerappnew.data.api.FutureScheduleFlightService
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.data.repository.futureSchedule.dataSource.FutureScheduleRemoteDataSource
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.airportCode
import com.example.flighttrackerappnew.presentation.utils.flightType
import com.example.flighttrackerappnew.presentation.utils.startDate

class FutureScheduleRemoteDataSourceImpl(
    private val futureScheduleFlightService: FutureScheduleFlightService
) : FutureScheduleRemoteDataSource {
    override suspend fun getFutureFlightData(): List<FutureScheduleItem> {
        val apiKey = RemoteConfigManager.getString("api_key")
        return futureScheduleFlightService.getSchedulesFlight(
            type = flightType,
            iataCode = airportCode.uppercase(),
            date = startDate,
            apiKey
        )
    }
}