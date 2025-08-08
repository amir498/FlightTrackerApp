package com.example.flighttrackerappnew.data.repository.airplane.datasource

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems

interface AirPlanesRemoteDataSource {
   suspend fun getStaticAirPlaneFromRemote(): List<AirPlaneItems>
}