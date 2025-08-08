package com.example.flighttrackerappnew.data.model.futureSchedule

import androidx.room.Embedded

data class Codeshared(
    @Embedded(prefix = "airline_") val airline: Airline?,
    @Embedded(prefix = "flight_") val flight: FlightX?
)