package com.example.liveflighttrackerapp.domain.usecase

import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import com.example.liveflighttrackerapp.domain.repository.CitiesRepository
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

class GetCitiesUseCase(private val citiesRepository: CitiesRepository) {
        suspend fun execute(): Resource<List<CitiesDataItems>> =
            citiesRepository.getCitiesData()
}