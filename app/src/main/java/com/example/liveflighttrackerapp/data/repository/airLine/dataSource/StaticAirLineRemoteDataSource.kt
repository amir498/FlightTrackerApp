package com.example.liveflighttrackerapp.data.repository.airLine.dataSource

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems

interface StaticAirLineRemoteDataSource {
   suspend fun getStaticAirLineFromRemote(): List<StaticAirLineItems>
}