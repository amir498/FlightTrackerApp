package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface AirPlanesRepository {
    suspend fun getAirPlaneData(): Resource<List<AirPlaneItems>>
}