package com.example.liveflighttrackerapp.domain.repository

import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

interface CitiesRepository {
    suspend fun getCitiesData(): Resource<List<CitiesDataItems>>
}