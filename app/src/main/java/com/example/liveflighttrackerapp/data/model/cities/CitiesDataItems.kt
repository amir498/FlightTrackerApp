package com.example.liveflighttrackerapp.data.model.cities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CitiesDataItems")
data class CitiesDataItems(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val GMT: String?,
    val cityId: Int?,
    val codeIataCity: String?,
    val codeIso2Country: String?,
    val geonameId: Int?,
    val latitudeCity: Double?,
    val longitudeCity: Double?,
    val nameCity: String?,
    val timezone: String?
)