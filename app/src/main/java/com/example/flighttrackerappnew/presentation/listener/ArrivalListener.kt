package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData

fun interface ArrivalListener {
    fun onclick(items: FullDetailFlightData)
}