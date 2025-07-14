package com.example.liveflighttrackerapp.domain.usecase

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.domain.repository.LiveFlightRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class GetLiveFlightUseCase(private val liveFlightRepository: LiveFlightRepository) {
        suspend fun execute(): Resource<List<FlightDataItem>> =
            liveFlightRepository.getLiveFlightData()
}