package com.example.flighttrackerappnew

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.flighttrackerappnew.data.repository.billing.BillingRepository
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase
import com.example.flighttrackerappnew.presentation.di.appModule
import com.example.flighttrackerappnew.presentation.lifecycle_observer.ActivitiesLifeCycleObserver
import com.example.flighttrackerappnew.presentation.lifecycle_observer.BillingLifecycleObserver
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FlightApp : Application() {

    companion object {
        var canRequestAd = false
    }

    override fun onCreate() {
        super.onCreate()

        ActivitiesLifeCycleObserver.apply {
            registerActivityLifecycleCallbacks(this)
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        }

        startKoin {
            androidContext(this@FlightApp)
            modules(appModule)
        }
        RemoteConfigManager.init()

        val billingRepo = BillingRepository(applicationContext)
        val billingUseCase = BillingUseCase(billingRepo)

        registerActivityLifecycleCallbacks(
            BillingLifecycleObserver(billingUseCase)
        )
    }
}