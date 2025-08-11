package com.example.liveflighttrackerapp.presentation.listener

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems

fun interface SearchAirportListener {
    fun onAirportSelected(airport: AirportsDataItems)
}