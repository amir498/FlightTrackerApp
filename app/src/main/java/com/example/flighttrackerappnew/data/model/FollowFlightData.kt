package com.example.flighttrackerappnew.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FollowFlightData")
data class FollowFlightData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val depTime: String?,
    val arrivalTime: String?,
    val depCityName: String?,
    val arrivalCityName: String?,
    val arrivalIataCode: String?,
    val depIataCode: String?,
    val speedValue: String?,
    val altitudeValue: String?,
    val airlineName: String?,
    val callSign: String?,
    val flightNum: String?,
    val airCraftIataNumber: String?,
    val time: String,
    val progress: Int,
    )
