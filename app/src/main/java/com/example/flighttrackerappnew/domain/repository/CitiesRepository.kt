package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface CitiesRepository {
    suspend fun getCitiesData(): Resource<List<CitiesDataItems>>
}