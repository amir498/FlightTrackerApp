package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.domain.repository.FlightScheduleRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetFlightScheduleUseCase(private val flightScheduleRepository:FlightScheduleRepository) {
    suspend fun execute(): Resource<List<FlightSchedulesItems>> =
        flightScheduleRepository.getScheduleFlightData()
}