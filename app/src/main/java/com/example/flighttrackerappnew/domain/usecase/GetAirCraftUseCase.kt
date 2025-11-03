package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.domain.repository.AirCraftRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetAirCraftUseCase(private val airCraftRepository: AirCraftRepository) {
    suspend fun execute(): Resource<List<AirPlaneItems>> =
        airCraftRepository.getAirCraftData()
}