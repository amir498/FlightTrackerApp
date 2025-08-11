package com.example.liveflighttrackerapp.domain.usecase

import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.domain.repository.ScheduleFlightRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class GetScheduleFlightUseCase(private val scheduleFlightRepository:ScheduleFlightRepository) {
    suspend fun execute(): Resource<List<SchedulesFlightsItems>> =
        scheduleFlightRepository.getScheduleFlightData()
}