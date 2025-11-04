package com.example.flighttrackerappnew.presentation.admob

import androidx.lifecycle.ProcessLifecycleOwner
import com.example.flighttrackerappnew.FlightApp
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

object AdManager {
    fun init() {
        RemoteConfigManager.init()
        FlightApp.instance.registerActivityLifecycleCallbacks(
            ActivitiesLifeCycleObserver
        )
        ProcessLifecycleOwner.get().lifecycle.addObserver(ActivitiesLifeCycleObserver)
    }
}