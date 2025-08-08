package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem

fun interface SearchAircraftListener {
    fun onAircraftSelected(flightData: FlightDataItem)
}