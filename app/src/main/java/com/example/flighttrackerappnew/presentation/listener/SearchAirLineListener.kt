package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems

fun interface SearchAirLineListener {
    fun onAirlineSelected(flightData: StaticAirLineItems)
}