package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface StaticAirLineRepository {
    suspend fun getStaticAirLineData(): Resource<List<StaticAirLineItems>>
}