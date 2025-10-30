package com.example.flighttrackerappnew.presentation.admob

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.SplashActivity
import com.example.flighttrackerappnew.presentation.admob.app_open.AppOpenAdManager
import com.example.flighttrackerappnew.presentation.admob.remote_config.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.loadAppOpen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.ref.WeakReference

object ActivitiesLifeCycleObserver : LifecycleEventObserver,
    Application.ActivityLifecycleCallbacks, KoinComponent {

    private var currentActivityRef: WeakReference<Activity>? = null
    private var adId: String? = null
    private val config: Config by inject()

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val activity = currentActivityRef?.get()
        when (event) {
            Lifecycle.Event.ON_CREATE -> {}

            Lifecycle.Event.ON_START -> {
                Log.d("MY--8TAG", "loadAppOpen: $loadAppOpen ")
                Log.d("MY--8TAG", "loadAppOpen: ${activity != null} ")
                if (!config.isPremiumUser && activity != null && activity !is SplashActivity && loadAppOpen && activity.isNetworkAvailable() && RemoteConfigManager.getBoolean(
                        "APP_OPEN"
                    )
                ) {
                    Log.d("MY--8TAG", "adId: $adId ")
                    adId?.let {
                        Log.d("MY--8TAG", "onStateChanged: LoadAppOpenCalled ")
                        AppOpenAdManager.loadAndShowAppOpen(activity, it)
                    }
                }
            }

            Lifecycle.Event.ON_RESUME -> {}

            Lifecycle.Event.ON_PAUSE -> {}

            Lifecycle.Event.ON_STOP -> {}

            Lifecycle.Event.ON_DESTROY -> {}

            Lifecycle.Event.ON_ANY -> {}
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

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

    override fun onActivityDestroyed(activity: Activity) {}
}