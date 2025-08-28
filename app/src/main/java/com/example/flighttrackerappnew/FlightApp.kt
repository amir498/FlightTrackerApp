package com.example.flighttrackerappnew

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.di.appModule
import com.example.flighttrackerappnew.presentation.lifecycle_observer.ActivitiesLifeCycleObserver
import com.example.flighttrackerappnew.presentation.lifecycle_observer.BillingLifecycleObserver
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FlightApp : Application() {
    companion object {
        var canRequestAd = false
    }

    private val billingUseCase: BillingUseCase by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FlightApp)
            modules(appModule)
        }
        RemoteConfigManager.init()

        registerActivityLifecycleCallbacks(ActivitiesLifeCycleObserver)
        ProcessLifecycleOwner.get().lifecycle.addObserver(ActivitiesLifeCycleObserver)

        registerActivityLifecycleCallbacks(BillingLifecycleObserver(billingUseCase))

        InterstitialAdManager.init(getKoin())
    }
}