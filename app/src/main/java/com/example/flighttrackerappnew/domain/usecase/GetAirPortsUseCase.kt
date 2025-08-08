package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.domain.repository.AirPortsRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetAirPortsUseCase(private val airPortsRepository: AirPortsRepository) {
        suspend fun execute(): Resource<List<AirportsDataItems>> =
            airPortsRepository.getAirportsData()
}