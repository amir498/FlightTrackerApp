package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface AirCraftRepository {
    suspend fun getAirCraftData(): Resource<List<AirPlaneItems>>
}