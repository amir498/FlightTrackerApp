package com.example.liveflighttrackerapp.data.model.schedulesFlight

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SchedulesFlightsItems")
data class SchedulesFlightsItems(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @Embedded(prefix = "airline_")
    val airline: Airline?,

    @Embedded(prefix = "arrival_")
    val arrival: Arrival?,

    @Embedded(prefix = "departure_")
    val departure: Departure?,

    @Embedded(prefix = "flight_")
    val flight: Flight?,

    val status: String?,
    val type: String?
)

