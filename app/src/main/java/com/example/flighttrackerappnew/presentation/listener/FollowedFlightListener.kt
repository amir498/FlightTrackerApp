package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData

interface FollowedFlightListener {

    fun onUnFollowClicked(data: FullDetailFlightData)
    fun onViewDetailedClicked(data: FullDetailFlightData)
}