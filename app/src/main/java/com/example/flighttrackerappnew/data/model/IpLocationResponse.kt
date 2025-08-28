package com.example.flighttrackerappnew.data.model

data class IpLocationResponse(
    val status: String,
    val country: String,
    val countryCode: String,
    val regionName: String,
    val city: String,
    val lat: Double,
    val lon: Double,
    val query: String
)
