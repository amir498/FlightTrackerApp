package com.example.liveflighttrackerapp.domain.usecase

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.domain.repository.StaticAirLineRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class GetStaticAirLineUseCase(private val staticAirLineRepository: StaticAirLineRepository) {
        suspend fun execute(): Resource<List<StaticAirLineItems>> =
            staticAirLineRepository.getStaticAirLineData()
}