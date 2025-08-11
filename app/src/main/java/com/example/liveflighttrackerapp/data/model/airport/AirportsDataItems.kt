package com.example.liveflighttrackerapp.data.model.airport

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AirportsDataItems")
data class AirportsDataItems(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val GMT: String?,
    val airportId: Int?,
    val codeIataAirport: String?,
    val codeIataCity: String?,
    val codeIcaoAirport: String?,
    val codeIso2Country: String?,
    val geonameId: String?,
    val latitudeAirport: Double?,
    val longitudeAirport: Double?,
    val nameAirport: String?,
    val nameCountry: String?,
    val phone: String?,
    val timezone: String?
)
