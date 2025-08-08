package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.domain.repository.AirPlanesRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetAirPlanesUseCase(private val airPlanesRepository: AirPlanesRepository) {
    suspend fun execute(): Resource<List<AirPlaneItems>> =
        airPlanesRepository.getAirPlaneData()
}