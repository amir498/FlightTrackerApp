package com.example.flighttrackerappnew.presentation.listener

import com.example.flighttrackerappnew.data.model.FollowFlightData

interface FollowedFlightListener {

    fun onUnFollowClicked(data: FollowFlightData)
    fun onViewDetailedClicked(data: FollowFlightData)
}