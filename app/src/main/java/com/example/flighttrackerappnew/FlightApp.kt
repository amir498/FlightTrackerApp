package com.example.flighttrackerappnew

import android.app.Application
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase2
import com.example.flighttrackerappnew.presentation.admob.AdManager
import com.example.flighttrackerappnew.presentation.di.appModule
import com.example.flighttrackerappnew.presentation.lifecycle_observer.BillingLifecycleObserverCombined
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FlightApp : Application() {

    companion object {
        lateinit var instance: FlightApp
            private set
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FlightApp)
            modules(appModule)
        }

        instance = this
        AdManager.init()

        val billingUseCase: BillingUseCase = getKoin().get()
        val billingUseCase2: BillingUseCase2 = getKoin().get()

        registerActivityLifecycleCallbacks(
            BillingLifecycleObserverCombined(
                billingUseCase,
                billingUseCase2
            )
        )
    }
}