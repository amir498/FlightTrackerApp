package com.example.liveflighttrackerapp.data.model.schedulesFlight

data class Arrival(
    val actualRunway: String?,
    val actualTime: String?,
    val baggage: String?,
    val delay: String?,
    val estimatedRunway: String?,
    val estimatedTime: String?,
    val gate: String?,
    val iataCode: String?,
    val icaoCode: String?,
    val scheduledTime: String?,
    val terminal: String?
)
