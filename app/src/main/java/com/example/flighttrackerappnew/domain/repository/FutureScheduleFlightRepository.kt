package com.example.flighttrackerappnew.domain.repository

import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource

interface FutureScheduleFlightRepository {
    suspend fun getFutureScheduleFlightData(): Resource<List<FutureScheduleItem>>
}