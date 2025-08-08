package com.example.flighttrackerappnew.data.model.futureSchedule

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FutureScheduleFlight")
data class FutureScheduleItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded(prefix = "aircraft_") val aircraft: Aircraft?,
    @Embedded(prefix = "airline_") val airline: Airline?,
    @Embedded(prefix = "arrival_") val arrival: Arrival?,
    @Embedded(prefix = "codeshared_") val codeshared: Codeshared?,
    @Embedded(prefix = "departure_") val departure: Departure?,
    @Embedded(prefix = "flight_") val flight: FlightX?,
    @Embedded(prefix = "weekday_") val weekday: String?
)