package com.example.liveflighttrackerapp.data.model.tracking

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TrackedDataItem")
data class TrackedDataItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val callSign: String,
    val depIataCode: String,
    val arrivalIataCode: String,
    val flightNum: String,
    val AirCraftiataNumber: String,
    val depTime: String,
    val arriTime: String
)
