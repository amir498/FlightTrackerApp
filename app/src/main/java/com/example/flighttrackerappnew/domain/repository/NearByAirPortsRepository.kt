package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface NearByAirPortsRepository {
    suspend fun getNearbyData(lat: Double,long: Double,distance: Int): Resource<List<NearByAirportsDataItems>>
}