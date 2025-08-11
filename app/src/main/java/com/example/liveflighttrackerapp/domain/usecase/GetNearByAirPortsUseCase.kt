package com.example.liveflighttrackerapp.domain.usecase

import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems
import com.example.liveflighttrackerapp.domain.repository.NearByAirPortsRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class GetNearByAirPortsUseCase(private val nearByAirPortsRepository: NearByAirPortsRepository) {
        suspend fun execute(): Resource<List<NearByAirportsDataItems>> =
            nearByAirPortsRepository.getNearbyData()
}