package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.repository.flightScheduleForSpecificIataNo.FlightScheduleRepositoryForSpecificIataNoImpl
import com.example.flighttrackerappnew.domain.repository.FlightScheduleForSpecificIataNoRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetFlightScheduleForSpecificIataNumberUseCase(private val flightScheduleForSpecificIataNoRepository:FlightScheduleForSpecificIataNoRepository) {
    suspend fun execute(iataNo: String): Resource<List<FlightSchedulesItems>> =
        flightScheduleForSpecificIataNoRepository.getScheduleFlightDataForSpecificIataNumber(iataNo)
}