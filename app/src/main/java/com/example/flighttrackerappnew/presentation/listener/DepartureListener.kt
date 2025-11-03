package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData

fun interface DepartureListener {
    fun onclick(items: FullDetailFlightData)
}