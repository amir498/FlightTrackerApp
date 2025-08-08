package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.arrival.ArrivalDataItems

fun interface ArrivalListener {
    fun onclick(items: ArrivalDataItems)
}