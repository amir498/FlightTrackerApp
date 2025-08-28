package com.example.flighttrackerappnew.presentation.adManager.interstitial

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.clickCount
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.loadAppOpen
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.UserMessagingPlatform
import org.koin.core.Koin

object InterstitialAdManager {
    var mInterstitialAd: InterstitialAd? = null
    lateinit var config: Config

    fun init(koin: Koin) {
        config = koin.get()
    }

    private var onAdDismissed: (() -> Unit)? = null
    fun loadInterstitialAd(
        requireContext: Context,
        adUnitId: String,
        activity: Activity,
        onAdLoaded: (() -> Unit)?,
        onAdFailed: (() -> Unit)?,
        onAdDismissed: (() -> Unit)?,
    ) {
        this.onAdDismissed = onAdDismissed
        if (requireContext.isNetworkAvailable() && clickCount % 2 == 0 && mInterstitialAd == null && !config.isPremiumUser) {
            val consentInformation = UserMessagingPlatform.getConsentInformation(requireContext)
            val canRequestAds = consentInformation.canRequestAds()
            if (canRequestAds) {
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(
                    requireContext,
                    adUnitId,
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            mInterstitialAd = interstitialAd
                            mInterstitialAd?.setImmersiveMode(true)
                            onAdLoaded?.invoke()
                            setupFullScreenContentCallback()
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            loadAppOpen = true
                            onAdFailed?.invoke()
                        }
                    })
            }
        }
    }

    fun showAd(activity: Activity, onAdDismissed: (() -> Unit)?) {
        onAdDismissed?.let {
            this.onAdDismissed = onAdDismissed
        }

        mInterstitialAd?.let {
            val dialog = showDialogForAd(activity)
            Handler(Looper.getMainLooper()).postDelayed({
                it.setImmersiveMode(true)
                it.show(activity)

                Handler(Looper.getMainLooper()).postDelayed({
                    if (dialog.isShowing) {
                        dialog.dismiss()
                    }
                }, 500)

            }, 1000)
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
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {}

            override fun onAdDismissedFullScreenContent() {
                onAdDismissed?.invoke()
                mInterstitialAd = null
                loadAppOpen = true
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                loadAppOpen = false
            }

            override fun onAdShowedFullScreenContent() {}
        }
    }
}