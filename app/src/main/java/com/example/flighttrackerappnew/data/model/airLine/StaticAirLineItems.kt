package com.example.flighttrackerappnew.data.model.airLine

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StaticAirLineItems")
data class StaticAirLineItems(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ageFleet: Double?,
    val airlineId: Int?,
    val callsign: String?,
    val codeHub: String?,
    val codeIataAirline: String?,
    val codeIcaoAirline: String?,
    val codeIso2Country: String?,
    val founding: Int?,
    val iataPrefixAccounting: String?,
    val nameAirline: String?,
    val nameCountry: String?,
    val sizeAirline: Int?,
    val statusAirline: String?,
    val type: String?
)
