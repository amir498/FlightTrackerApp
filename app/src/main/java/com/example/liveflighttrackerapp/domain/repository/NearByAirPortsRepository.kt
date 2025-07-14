package com.example.liveflighttrackerapp.domain.repository

import com.example.liveflighttrackerapp.data.model.nearby.NearByAirportsDataItems
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

interface NearByAirPortsRepository {
    suspend fun getNearbyData(): Resource<List<NearByAirportsDataItems>>
}