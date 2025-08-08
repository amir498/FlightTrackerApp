package com.example.flighttrackerappnew.presentation.adManager.banner

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import android.widget.TextView
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.ump.UserMessagingPlatform

class BannerAdManager {
    private lateinit var adView: AdView

    fun loadAd(
        isCollapsible: Boolean = true,
        context: Context,
        bannerId: String,
        onAdLoaded: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        adView = AdView(context)
        if (context.isNetworkAvailable()) {
            adView.adUnitId = bannerId
            adView.setAdSize(getAdSize(context))

            val consentInformation = UserMessagingPlatform.getConsentInformation(context)
            val canRequestAds = consentInformation.canRequestAds()
            if (canRequestAds) {
                val adRequest = if (isCollapsible) {
                    val extras = Bundle()
                    extras.putString("collapsible", "bottom")
                    AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                        .build()
                } else {
                    AdRequest.Builder().build()
                }
                adView.loadAd(adRequest)
                adListener(onAdLoaded, onAdFailed)
            }
        } else {
            onAdFailed()
        }
    }

    fun showBannerAd(
        adContainerView: FrameLayout,
        requireContext: Context,
        bannerAdLoadingView: TextView?
    ) {
        if (requireContext.isNetworkAvailable()) {
            adContainerView.removeAllViews()
            adContainerView.addView(adView)
        } else {
            adContainerView.gone()
            bannerAdLoadingView?.gone()
        }
    }

    private fun adListener(onAdLoaded: () -> Unit, onAdFailed: () -> Unit) {
        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
            }

            override fun onAdClosed() {
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                onAdFailed()
            }

            override fun onAdImpression() {
            }

            override fun onAdLoaded() {
                onAdLoaded()
            }

            override fun onAdOpened() {

            }
        }
    }

    private fun getAdSize(context: Context): AdSize {
        val outMetrics: DisplayMetrics = context.resources.displayMetrics
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }
}