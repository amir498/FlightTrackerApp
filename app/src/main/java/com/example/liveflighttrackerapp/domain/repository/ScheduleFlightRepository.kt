package com.example.liveflighttrackerapp.domain.repository

import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource

interface ScheduleFlightRepository {
    suspend fun getScheduleFlightData(): Resource<List<SchedulesFlightsItems>>
}