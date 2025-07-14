package com.example.liveflighttrackerapp.data.repository.airplane.datasource

import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems

interface AirPlanesRemoteDataSource {
   suspend fun getStaticAirPlaneFromRemote(): List<AirPlaneItems>
}