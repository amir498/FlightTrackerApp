package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface FlightScheduleRepository {
    suspend fun getScheduleFlightData(): Resource<List<FlightSchedulesItems>>
}