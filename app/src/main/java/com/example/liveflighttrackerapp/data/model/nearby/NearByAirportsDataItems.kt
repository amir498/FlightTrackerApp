package com.example.liveflighttrackerapp.data.model.nearby

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NearByCitiesDataItems")
data class NearByAirportsDataItems(
    @PrimaryKey var id: Int = 0,
    val GMT: String?,
    val codeIataAirport: String?,
    val codeIataCity: String?,
    val codeIcaoAirport: String?,
    val codeIso2Country: String?,
    val distance: Double?,
    val latitudeAirport: Double?,
    val longitudeAirport: Double?,
    val nameAirport: String?,
    val nameCountry: String?,
    val phone: String?,
    val timezone: String?
)
