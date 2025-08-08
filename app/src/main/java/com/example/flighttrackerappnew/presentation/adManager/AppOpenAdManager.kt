package com.example.flighttrackerappnew.presentation.adManager

import android.app.Activity
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.ump.UserMessagingPlatform

object AppOpenAdManager {

    private var appOpenAd: AppOpenAd? = null
    var isLoading: Boolean = false

    private lateinit var onAdLaded: () -> Unit

    private var show = false

    fun loadAppOpenAd(context: Activity, adId: String) {
        if (context.isNetworkAvailable() && appOpenAd == null && !isLoading) {
            isLoading = true
            val consentInformation = UserMessagingPlatform.getConsentInformation(context)
            val canRequestAds = consentInformation.canRequestAds()
            if (canRequestAds) {
                val adRequest = AdRequest.Builder().build()
                AppOpenAd.load(
                    context,
                    adId,
                    adRequest,
                    object : AppOpenAdLoadCallback() {
                        override fun onAdLoaded(ad: AppOpenAd) {
                            appOpenAd = ad
                            isLoading = false
                            setupFullScreenContentCallback()
                            if (show) {
                                onAdLaded.invoke()
                                show = false
                            }
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            isLoading = false
                            appOpenAd = null
                            show = false
                        }
                    })
            }
        }
    }

    private fun setupFullScreenContentCallback() {
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                show = false
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                appOpenAd = null
                show = false
            }

            override fun onAdShowedFullScreenContent() {
            }
        }
    }

    fun showAppOpenAd(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) return
        if (appOpenAd != null) {
            appOpenAd?.show(activity)
        }
    }
}
