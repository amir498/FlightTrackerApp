package com.example.liveflighttrackerapp.presentation.listener

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem

fun interface SearchAircraftListener {
    fun onAircraftSelected(flightData: FlightDataItem)
}