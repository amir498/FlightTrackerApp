package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.domain.repository.StaticAirLineRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetStaticAirLineUseCase(private val staticAirLineRepository: StaticAirLineRepository) {
        suspend fun execute(): Resource<List<StaticAirLineItems>> =
            staticAirLineRepository.getStaticAirLineData()
}