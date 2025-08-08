package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.domain.repository.FutureScheduleFlightRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetFutureScheduleFlightUseCase(private val futureScheduleFlightRepository: FutureScheduleFlightRepository) {
    suspend fun execute(): Resource<List<FutureScheduleItem>> =
        futureScheduleFlightRepository.getFutureScheduleFlightData()
}