package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems

fun interface SearchTailNumberListener {
    fun onTailSelected(flightData: AirPlaneItems?)
}