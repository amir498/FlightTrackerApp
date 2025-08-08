package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems

fun interface AirPortClickListener {
    fun onAirportClick(nearByAirportsData: NearByAirportsDataItems)
}