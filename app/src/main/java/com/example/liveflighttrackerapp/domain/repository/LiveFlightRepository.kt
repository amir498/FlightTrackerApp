package com.example.liveflighttrackerapp.domain.repository

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

interface LiveFlightRepository {
    suspend fun getLiveFlightData(): Resource<List<FlightDataItem>>
}