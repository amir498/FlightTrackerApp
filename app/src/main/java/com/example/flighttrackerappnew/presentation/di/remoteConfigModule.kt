package com.example.flighttrackerappnew.presentation.di

import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager2
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import org.koin.dsl.module

val remoteConfigModule = module {

    single<FirebaseRemoteConfig> {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig
    }

    single { RemoteConfigManager2(get()) }
}