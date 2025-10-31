package com.example.flighttrackerappnew.presentation.lifecycle_observer

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase2
import com.example.flighttrackerappnew.presentation.activities.beforeHome.SplashActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2

class BillingLifecycleObserverCombined(
    private val billingUseCase: BillingUseCase,
    private val billingUseCase2: BillingUseCase2
) : ActivityLifecycleCallbacks {

    override fun onActivityResumed(activity: Activity) {
        when (activity) {
            is PremiumActivity -> {
                billingUseCase.apply {
                    startConnection {
                        queryPurchase()
                        getProductDetails()
                    }
                }
            }

            is PremiumActivity2 -> {
                billingUseCase2.apply {
                    startConnection {
                        queryPurchase()
                        getProductDetails()
                    }
                }
            }

            is SplashActivity -> {
                billingUseCase2.apply {
                    startConnection {
                        queryPurchase()
                        getProductDetails()
                    }
                }
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
//        when (activity) {
//            is PremiumActivity -> billingUseCase.releaseBilling()
//            is PremiumActivity2 -> billingUseCase2.releaseBilling()
//        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}