package com.example.liveflighttrackerapp

import android.app.Application
import com.example.liveflighttrackerapp.presentation.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FlightApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FlightApp)
            modules(appModule)
        }
    }
}