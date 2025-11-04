package com.example.flighttrackerappnew.presentation.remoteconfig

import com.example.flighttrackerappnew.FlightApp
import com.example.flighttrackerappnew.R
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object RemoteConfigManager {

    val remoteConfig by lazy { Firebase.remoteConfig }

    fun init() {
        remoteConfig.apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds =
                        FlightApp.instance.resources
                            .getInteger(R.integer.minimumFetchIntervalInSeconds)
                            .toLong()
                }
            )
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate()
                .addOnCompleteListener { task -> }
        }
    }

    fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }

    fun getString(key: String): String {
        return remoteConfig.getString(key)
    }

    fun getNumber(key: String): Long {
        return remoteConfig.getLong(key)
    }
}
