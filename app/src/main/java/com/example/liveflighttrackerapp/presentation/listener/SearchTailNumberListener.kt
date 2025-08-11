package com.example.liveflighttrackerapp.presentation.listener

import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems

fun interface SearchTailNumberListener {
    fun onTailSelected(flightData: AirPlaneItems)
}