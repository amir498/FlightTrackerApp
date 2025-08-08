package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems

fun interface SearchAirportListener {
    fun onAirportSelected(airport: AirportsDataItems)
}