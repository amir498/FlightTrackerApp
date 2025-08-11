package com.example.liveflighttrackerapp.presentation.listener

import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem

fun interface AirPlaneClickListener {
    fun onPlaneClick(flightData: FlightDataItem)
}