package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.domain.repository.LiveFlightRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetLiveFlightUseCase(private val liveFlightRepository: LiveFlightRepository) {
        suspend fun execute(): Resource<List<FlightDataItem>> =
            liveFlightRepository.getLiveFlightData()
}