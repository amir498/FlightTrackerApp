package com.example.liveflighttrackerapp.domain.repository

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

interface StaticAirLineRepository {
    suspend fun getStaticAirLineData(): Resource<List<StaticAirLineItems>>
}