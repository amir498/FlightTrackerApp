package com.example.liveflighttrackerapp.domain.usecase

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.domain.repository.AirPortsRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class GetAirPortsUseCase(private val airPortsRepository: AirPortsRepository) {
        suspend fun execute(): Resource<List<AirportsDataItems>> =
            airPortsRepository.getAirportsData()
}