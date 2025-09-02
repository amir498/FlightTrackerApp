package com.example.flighttrackerappnew.data.api

import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightSchedulesService {
    @GET("timetable")
    suspend fun getSchedulesFlight(
        @Query("key") apiKey: String
    ): List<FlightSchedulesItems>
}