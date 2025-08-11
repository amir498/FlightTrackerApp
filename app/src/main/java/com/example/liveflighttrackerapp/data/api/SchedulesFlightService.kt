package com.example.liveflighttrackerapp.data.api

import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import retrofit2.http.GET

interface SchedulesFlightService {
    @GET("timetable")
    suspend fun getSchedulesFlight(
    ): List<SchedulesFlightsItems>
}