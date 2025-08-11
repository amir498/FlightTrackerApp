package com.example.liveflighttrackerapp.presentation.listener

import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems

fun interface SearchAirLineListener {
    fun onAirlineSelected(flightData: StaticAirLineItems)
}