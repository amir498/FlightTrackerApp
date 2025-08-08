package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.domain.repository.NearByAirPortsRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetNearByAirPortsUseCase(private val nearByAirPortsRepository: NearByAirPortsRepository) {
        suspend fun execute(): Resource<List<NearByAirportsDataItems>> =
            nearByAirPortsRepository.getNearbyData()
}