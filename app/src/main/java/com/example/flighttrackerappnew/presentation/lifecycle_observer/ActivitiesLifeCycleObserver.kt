package com.example.flighttrackerappnew.presentation.lifecycle_observer

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.PrivacyPolicyActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.SplashActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.adManager.AppOpenAdManager
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager.config
import com.example.flighttrackerappnew.presentation.utils.loadAppOpen
import java.lang.ref.WeakReference

object ActivitiesLifeCycleObserver : LifecycleEventObserver, ActivityLifecycleCallbacks {
    private var currentActivityRef: WeakReference<Activity>? = null
    private var adId: String? = null

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                val activity = currentActivityRef?.get()
                if (activity != null) {
                    AppOpenAdManager.showAppOpenAd(activity)
                } else {
                    Log.d("MY--TAG", "onStateChanged:ON_START First Time")
                }
            }

            Lifecycle.Event.ON_STOP -> {
                val activity = currentActivityRef?.get()
                if (activity is SplashActivity || activity is PrivacyPolicyActivity || activity is PremiumActivity) return
                if (activity != null && adId != null && loadAppOpen && !config.isPremiumUser) {
                    AppOpenAdManager.loadAppOpenAd(activity, adId!!)
                } else {
                    Log.w("MY--TAG", "Activity is null. Cannot load app open ad.")
                }
            }

            else -> {}
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        val app = (activity as? BaseActivity<*>)?.app
        adId = app?.getString(R.string.APP_OPEN)
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        val app = (activity as? BaseActivity<*>)?.app
        adId = app?.getString(R.string.APP_OPEN)
        currentActivityRef = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
    }
}