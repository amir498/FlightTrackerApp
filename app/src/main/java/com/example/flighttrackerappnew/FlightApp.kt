package com.example.flighttrackerappnew

import android.app.Application
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase
import com.example.flighttrackerappnew.presentation.admob.AdManager
import com.example.flighttrackerappnew.presentation.di.appModule
import com.example.flighttrackerappnew.presentation.di.remoteConfigModule
import com.example.flighttrackerappnew.presentation.lifecycle_observer.BillingLifecycleObserver
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FlightApp : Application() {

    companion object {
        lateinit var instance: FlightApp
            private set
    }

    private val billingUseCase: BillingUseCase by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FlightApp)
            modules(appModule, remoteConfigModule)
        }

        instance = this
        AdManager.init()

        registerActivityLifecycleCallbacks(BillingLifecycleObserver(billingUseCase))
    }
}