package com.example.liveflighttrackerapp.domain.usecase

import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.domain.repository.AirPlanesRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class GetAirPlanesUseCase(private val airPlanesRepository: AirPlanesRepository) {
    suspend fun execute(): Resource<List<AirPlaneItems>> =
        airPlanesRepository.getAirPlaneData()
}