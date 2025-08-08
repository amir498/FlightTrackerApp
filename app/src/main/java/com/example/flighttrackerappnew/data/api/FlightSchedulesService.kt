package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import retrofit2.http.GET

interface FlightSchedulesService {
    @GET("timetable")
    suspend fun getSchedulesFlight(
    ): List<FlightSchedulesItems>
}