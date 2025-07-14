package com.example.liveflighttrackerapp.presentation.listener

import com.example.liveflighttrackerapp.data.model.arrival.ArrivalDataItems

fun interface ArrivalListener {
    fun onclick(items: ArrivalDataItems)
}