package com.example.flighttrackerappnew.data.model.futureSchedule

data class Departure(
    val gate: String?,
    val iataCode: String?,
    val icaoCode: String?,
    val scheduledTime: String?,
    val terminal: String?
)