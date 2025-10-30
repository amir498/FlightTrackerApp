package com.example.flighttrackerappnew.presentation.admob.banner

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.admob.native.AdStatus
import com.example.flighttrackerappnew.presentation.admob.native.AdUnit
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BannerAdUnit(
    id: String,
    name: String,
    bannerAdType: BannerAdTypes
) : AdUnit<AdView, BannerAdTypes>(id, name, bannerAdType) {

    suspend fun loadAd(
        context: Context,
        adContainerView: FrameLayout,
        onStartLoadingAd: () -> Unit
    ) {
        if (shouldLoadAd()) {
            _statusFlow.value = AdStatus.Loading
            val adLoadedPair = loadBannerAd(context, id, adContainerView, onStartLoadingAd)
            val loadedAd = adLoadedPair.first
            if (loadedAd != null) {
                ad = loadedAd
                _statusFlow.value = AdStatus.Ready
                showBannerAd(ad, adContainerView)
                Log.d("AD-TAG", "Ad Status:$name ==> ${AdStatus.Ready}")
            } else {
                _statusFlow.value = AdStatus.Failure
                Log.d("AD-TAG", "Ad Status:$name ==> ${AdStatus.Failure}")
            }
        } else {
            Log.d("AD-TAG", "Ad Status:$name ==> Skipped loading (status=$status)")
        }
    }

    suspend fun loadBannerAd(
        context: Context,
        adId: String,
        adContainerView: FrameLayout,
        onStartLoadingAd: () -> Unit
    ): Pair<AdView?, LoadAdError?> = suspendCancellableCoroutine { continuation ->

        val adView = AdView(context).apply {
            adUnitId = adId
            setAdSize(getAdSize(context))
        }
        val adRequest: AdRequest = when (adType) {
            BannerAdTypes.SIMPLE -> {
                AdRequest.Builder().build()
            }

            BannerAdTypes.COLLAPSIBLE -> {
                val extras = Bundle()
                extras.putString("collapsible", "bottom")
                AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                    .build()
            }
        }

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("AD-TAG", "Banner Ad Loaded: $name")
                if (continuation.isActive) continuation.resume(adView to null)
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.d("AD-TAG", "Banner Ad Failed: $name => ${error.message}")
                if (continuation.isActive) continuation.resume(null to error)
            }

            override fun onAdImpression() {
                _statusFlow.value = AdStatus.Shown
            }
        }

        try {
            onStartLoadingAd.invoke()

            CoroutineScope(Dispatchers.Main).launch {
                val shimmerLayout = LayoutInflater.from(context)
                    .inflate(R.layout.load_fb_banner, adContainerView, false)
                adContainerView.removeAllViews()
                adContainerView.addView(shimmerLayout)
                adView.loadAd(adRequest)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            if (continuation.isActive) {
                continuation.resume(
                    null to LoadAdError(
                        1,
                        e.message ?: "Error",
                        "AdView",
                        null,
                        null
                    )
                )
            }
        }

        continuation.invokeOnCancellation {
            adView.destroy()
        }
    }


    fun showBannerAd(adView: AdView?, adContainerView: FrameLayout) {
        CoroutineScope(Dispatchers.Main).launch {
            adContainerView.removeAllViews()
            adContainerView.addView(adView)
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