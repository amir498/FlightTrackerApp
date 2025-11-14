package com.example.flighttrackerappnew.data.repository.flightScheduleForSpecificIataNo.dataSource

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems

interface FlightScheduleRemoteDataSourceForSpecificIataNumber {
    suspend fun getScheduleFlightDataForSpecificIataNumber(iataNo: String): List<FlightSchedulesItems>
}