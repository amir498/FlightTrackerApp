package com.example.liveflighttrackerapp.data.model.airplane

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AirPlaneItems")
data class AirPlaneItems(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val airplaneIataType: String?,
    val airplaneId: Int?,
    val codeIataAirline: String?,
    val codeIataPlaneLong: String?,
    val codeIataPlaneShort: String?,
    val codeIcaoAirline: String?,
    val constructionNumber: String?,
    val deliveryDate: String?,
    val enginesCount: String?,
    val enginesType: String?,
    val firstFlight: String?,
    val hexIcaoAirplane: String?,
    val lineNumber: String?,
    val modelCode: String?,
    val numberRegistration: String?,
    val numberTestRgistration: String?,
    val planeAge: String?,
    val planeModel: String?,
    val planeOwner: String?,
    val planeSeries: String?,
    val planeStatus: String?,
    val productionLine: String?,
    val registrationDate: String?,
    val rolloutDate: String?
)