package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.domain.repository.CitiesRepository
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

class GetCitiesUseCase(private val citiesRepository: CitiesRepository) {
        suspend fun execute(): Resource<List<CitiesDataItems>> =
            citiesRepository.getCitiesData()
}