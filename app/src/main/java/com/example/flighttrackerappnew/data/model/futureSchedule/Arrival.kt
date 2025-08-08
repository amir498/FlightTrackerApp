package com.example.flighttrackerappnew.data.model.futureSchedule

data class Arrival(
    val gate: String?,
    val iataCode: String?,
    val icaoCode: String?,
    val scheduledTime: String?,
    val terminal: String?
)