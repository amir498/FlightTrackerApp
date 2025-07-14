package com.example.liveflighttrackerapp.domain.repository

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

interface AirPortsRepository {
    suspend fun getAirportsData(): Resource<List<AirportsDataItems>>
}