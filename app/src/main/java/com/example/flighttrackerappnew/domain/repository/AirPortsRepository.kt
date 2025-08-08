package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface AirPortsRepository {
    suspend fun getAirportsData(): Resource<List<AirportsDataItems>>
}