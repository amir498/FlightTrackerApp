package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData

interface FavFlightListener {

    fun onUnFavClicked(data: FullDetailFlightData)
    fun onViewDetailedClicked(data: FullDetailFlightData)
}