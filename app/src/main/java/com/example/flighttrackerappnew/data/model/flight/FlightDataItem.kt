package com.example.flighttrackerappnew.data.model.flight

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FlightDataItem")
data class FlightDataItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded(prefix = "aircraft_") val aircraft: Aircraft?,
    @Embedded(prefix = "airline_") val airline: Airline?,
    @Embedded(prefix = "arrival_") val arrival: Arrival?,
    @Embedded(prefix = "departure_") val departure: Departure?,
    @Embedded(prefix = "flight_") val flight: Flight?,
    @Embedded(prefix = "geo_") val geography: Geography?,
    @Embedded(prefix = "speed_") val speed: Speed?,
    val status: String?,
    @Embedded(prefix = "system_") val system: System?
)
