package com.example.flighttrackerappnew.presentation.admob

import androidx.lifecycle.ProcessLifecycleOwner
import com.example.flighttrackerappnew.FlightApp
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.initializeMobileAdsOnce

object AdManager {
    fun init(config: Config) {
        RemoteConfigManager.init()
        FlightApp.instance.registerActivityLifecycleCallbacks(
            ActivitiesLifeCycleObserver
        )
        ProcessLifecycleOwner.get().lifecycle.addObserver(ActivitiesLifeCycleObserver)
        if (!config.isPremiumUser){
            initializeMobileAdsOnce(FlightApp.instance.applicationContext)
        }
    }
}