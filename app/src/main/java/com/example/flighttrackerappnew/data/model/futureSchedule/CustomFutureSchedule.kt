package com.example.flighttrackerappnew.data.model.futureSchedule

data class CustomFutureSchedule(
    val flightNo: String,
    val airLineIataCode: String,
    val departureCity: String,
    val departureCityIataCode: String,
    val arrivalCity: String,
    val arrivalCityIataCode: String,
    val departureTime: String,
    val arrivalTime: String,
    val flightTime: String?,
    val type: Int = 1
)