package com.example.flighttrackerappnew.presentation.admob.app_open

import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.admob.ump.UMPConsentManager
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.canRequestAd
import com.example.flighttrackerappnew.presentation.utils.initializeMobileAdsOnce
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import org.koin.core.Koin

object AppOpenAdManager {

    var appOpenAd: AppOpenAd? = null
    var isLoading: Boolean = false

    private fun loadAppOpenAd(context: Activity, adId: String) {
        if (context.canRequestAd()) {
            loadAd(
                context, adId
            )
        } else {
            UMPConsentManager(context).apply {
                checkConsent { consentObtained ->
                    if (consentObtained) {
                        if (context.canRequestAd()) {
                            loadAd(
                                context, adId
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadAd(
        context: Activity, adId: String
    ) {
        (context).initializeMobileAdsOnce {
            if (appOpenAd == null && !isLoading) {
                isLoading = true
                val dialog = showDialogForAd(context)
                val adRequest = AdRequest.Builder().build()
                AppOpenAd.load(
                    context,
                    adId,
                    adRequest,
                    object : AppOpenAd.AppOpenAdLoadCallback() {
                        override fun onAdLoaded(ad: AppOpenAd) {
                            appOpenAd = ad
                            isLoading = false
                            setupFullScreenContentCallback()
                            showAppOpenAd(context)
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    dialog.dismiss()
                                }, 400
                            )
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            dialog.dismiss()
                            isLoading = false
                            appOpenAd = null
                        }
                    })
            }
        }
    }

    fun showDialogForAd(activity: Activity): Dialog {
        return CustomDialogBuilder(activity)
            .setLayout(R.layout.dialog_ad_loading)
            .setCancelable(true)
            .setPositiveClickListener {
            }.setNegativeClickListener {
            }.show(true)
    }

    private fun setupFullScreenContentCallback() {
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                appOpenAd = null
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

    fun loadAndShowAppOpen(activity: Activity, adId: String) {
        if (activity.isFinishing || activity.isDestroyed) return
        loadAppOpenAd(activity, adId)
    }
}