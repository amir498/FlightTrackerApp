package com.example.flighttrackerappnew.data.repository.airLine.dataSource

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems

interface StaticAirLineRemoteDataSource {
   suspend fun getStaticAirLineFromRemote(): List<StaticAirLineItems>
}