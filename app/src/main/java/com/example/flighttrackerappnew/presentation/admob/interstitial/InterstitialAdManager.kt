package com.example.flighttrackerappnew.presentation.admob.interstitial

import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.admob.ump.UMPConsentManager
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.canRequestAd
import com.example.flighttrackerappnew.presentation.utils.clickCount
import com.example.flighttrackerappnew.presentation.utils.initializeMobileAdsOnce
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.loadAppOpen
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object InterstitialAdManager : KoinComponent {
    var mInterstitialAd: InterstitialAd? = null
    private var onAdDismissed: (() -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private var showWhenReady: Boolean = false
    private var timeoutRunnable: Runnable? = null
    private var dialog: Dialog? = null
    private val config: Config by inject()

    fun loadInterstitialAd(
        ignoreClickCount: Boolean,
        showLoadingScreenWithDelay: Number,
        showLoadingAsLoadAdRequestCalled: Boolean = false,
        interstitialLoadingScreenShowTime: Long = 0L,
        showWhenReady: Boolean,
        activity: AppCompatActivity,
        adUnitId: String,
        isInterstitialEnabled: Boolean,
        adLoadingTimeOut: Long? = null,
        onAdDismissed: (() -> Unit)?,
        onStartLoadingAd: () -> Unit = {}
    ) {
        if (!config.isPremiumUser) {
            this.showWhenReady = showWhenReady
            if (activity.canRequestAd()) {
                if (clickCount % 2 == 0 || ignoreClickCount) {
                    loadAd(
                        interstitialLoadingScreenShowTime = interstitialLoadingScreenShowTime,
                        showLoadingAsLoadAdRequestCalled = showLoadingAsLoadAdRequestCalled,
                        showWhenReady = showWhenReady,
                        activity = activity,
                        adUnitId = adUnitId,
                        isInterstitialEnabled = isInterstitialEnabled,
                        adLoadingTimeOut = adLoadingTimeOut,
                        showLoadingScreenWithDelay = showLoadingScreenWithDelay.toLong(),
                        onAdDismissed,
                        onStartLoadingAd
                    )
                } else {
                    onAdDismissed?.invoke()
                }
            } else {
                UMPConsentManager(activity).apply {
                    checkConsent { consentObtained ->
                        if (consentObtained) {
                            if (activity.canRequestAd()) {
                                if (clickCount % 2 == 0 || ignoreClickCount) {
                                    loadAd(
                                        interstitialLoadingScreenShowTime = interstitialLoadingScreenShowTime,
                                        showLoadingAsLoadAdRequestCalled = showLoadingAsLoadAdRequestCalled,
                                        showWhenReady = showWhenReady,
                                        activity = activity,
                                        adUnitId = adUnitId,
                                        isInterstitialEnabled = isInterstitialEnabled,
                                        adLoadingTimeOut = adLoadingTimeOut,
                                        showLoadingScreenWithDelay = showLoadingScreenWithDelay.toLong(),
                                        onAdDismissed,
                                        onStartLoadingAd
                                    )
                                }
                            } else {
                                onAdDismissed?.invoke()
                            }
                        }
                    }
                }
            }
        } else {
            onAdDismissed?.invoke()
        }
    }

    fun loadAd(
        interstitialLoadingScreenShowTime: Long,
        showLoadingAsLoadAdRequestCalled: Boolean = false,
        showWhenReady: Boolean,
        activity: AppCompatActivity,
        adUnitId: String,
        isInterstitialEnabled: Boolean,
        adLoadingTimeOut: Long? = null,
        showLoadingScreenWithDelay: Long,
        onAdDismissed: (() -> Unit)?,
        onStartLoadingAd: () -> Unit
    ) {
        activity.initializeMobileAdsOnce {
            if (activity.isNetworkAvailable() && isInterstitialEnabled) {
                if (mInterstitialAd == null) {
                    if (showLoadingAsLoadAdRequestCalled) {
                        dialog = showDialogForAd(activity)
                    }
                    onStartLoadingAd.invoke()
                    this@InterstitialAdManager.onAdDismissed = onAdDismissed
                    val adRequestBuilder = AdRequest.Builder()
                    val adRequest = adRequestBuilder.build()
                    InterstitialAd.load(
                        activity,
                        adUnitId,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                Log.d("AD-TAG", "onAdLoaded: True")
                                mInterstitialAd = interstitialAd
                                mInterstitialAd?.setImmersiveMode(true)
                                setupFullScreenContentCallback()
                                if (this@InterstitialAdManager.showWhenReady) {
                                    this@InterstitialAdManager.showWhenReady = false
                                    timeoutRunnable?.let {
                                        handler.removeCallbacks(
                                            it
                                        )
                                    }
                                    timeoutRunnable = null
                                    showAd(
                                        interstitialLoadingScreenShowTime = interstitialLoadingScreenShowTime,
                                        showLoadingScreenAsLoadAdRequestCalled = showLoadingAsLoadAdRequestCalled,
                                        activity = activity,
                                        showLoadingScreenWithDelay = showLoadingScreenWithDelay,
                                        onAdDismissed = onAdDismissed
                                    )
                                }
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                Log.d(
                                    "AD-TAG",
                                    "onAdFailedToLoad Interstitial loadAdError :${loadAdError.message} "
                                )
                                finishFlow()
                            }
                        })
                    if (showWhenReady) {
                        adLoadingTimeOut?.let { timeout ->
                            timeoutRunnable = Runnable {
                                this@InterstitialAdManager.showWhenReady = false
                                this@InterstitialAdManager.onAdDismissed?.invoke()
                            }
                            handler.postDelayed(timeoutRunnable!!, timeout)
                        }
                    }
                }
            } else {
                onAdDismissed?.invoke()
            }
        }
    }

    fun showAd(
        interstitialLoadingScreenShowTime: Long,
        showLoadingScreenAsLoadAdRequestCalled: Boolean = false,
        activity: Activity,
        showLoadingScreenWithDelay: Long = 0L,
        onAdDismissed: (() -> Unit)?
    ) {
        loadAppOpen = false
        this.onAdDismissed = onAdDismissed
        mInterstitialAd?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                if (!showLoadingScreenAsLoadAdRequestCalled) {
                    if (showLoadingScreenWithDelay != 0L) {
                        dialog = showDialogForAd(activity)
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    it.setImmersiveMode(true)
                    it.show(activity)
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            dialog?.let {
                                if (it.isShowing) {
                                    it.dismiss()
                                }
                            }
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        }
                    }, 500)
                }, interstitialLoadingScreenShowTime)
            }, showLoadingScreenWithDelay)
        } ?: run {
            onAdDismissed?.invoke()
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
                finishFlow()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                finishFlow()
            }

            override fun onAdImpression() {
                loadAppOpen = false
            }

            override fun onAdShowedFullScreenContent() {}
        }
    }

    private fun finishFlow(invokeCallback: Boolean = true) {
        showWhenReady = false
        timeoutRunnable?.let { handler.removeCallbacks(it) }
        timeoutRunnable = null
        mInterstitialAd = null
        loadAppOpen = true
        if (invokeCallback) onAdDismissed?.invoke()
    }
}