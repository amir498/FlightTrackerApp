package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface LiveFlightRepository {
    suspend fun getLiveFlightData(latitude: Double, longitude: Double, distance: Int): Resource<List<FlightDataItem>>
}