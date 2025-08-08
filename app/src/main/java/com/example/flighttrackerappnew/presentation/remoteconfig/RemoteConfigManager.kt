package com.example.flighttrackerappnew.presentation.remoteconfig

import com.example.flighttrackerappnew.R
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object RemoteConfigManager {

    val remoteConfig by lazy { Firebase.remoteConfig }

    fun init() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }

    fun getString(key: String): String {
        return remoteConfig.getString(key)
    }
}
