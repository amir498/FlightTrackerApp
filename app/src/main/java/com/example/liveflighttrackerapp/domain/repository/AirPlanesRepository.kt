package com.example.liveflighttrackerapp.domain.repository

import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

interface AirPlanesRepository {
    suspend fun getAirPlaneData(): Resource<List<AirPlaneItems>>
}