package com.example.flighttrackerappnew.data.model

data class Country(
    val id: Int,
    val name: String,
    val iso2: String,
    val iso3: String,
    val capital: String?,
    val latitude: String,
    val longitude: String
)
