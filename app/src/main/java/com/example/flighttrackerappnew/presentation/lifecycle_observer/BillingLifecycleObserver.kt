package com.example.flighttrackerappnew.presentation.lifecycle_observer

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase
import java.lang.ref.WeakReference

class BillingLifecycleObserver(
    private val billingUseCase: BillingUseCase
) : ActivityLifecycleCallbacks {

    private var currentActivityRef: WeakReference<Activity>? = null

    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
        billingUseCase.initBilling {

        }
    }

    override fun onActivityPaused(activity: Activity) {
        billingUseCase.releaseBilling()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}