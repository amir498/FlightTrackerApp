package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.fav.FavFlightData

interface FavFlightListener {

    fun onUnFavClicked(data: FavFlightData)
    fun onViewDetailedClicked(data: FavFlightData)
}