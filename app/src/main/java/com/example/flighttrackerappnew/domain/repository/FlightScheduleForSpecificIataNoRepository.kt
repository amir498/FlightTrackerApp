package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface FlightScheduleForSpecificIataNoRepository {
    suspend fun getScheduleFlightDataForSpecificIataNumber(iataNo: String): Resource<List<FlightSchedulesItems>>
}