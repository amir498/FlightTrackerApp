package com.example.flighttrackerappnew.presentation.admob.native

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.resume

class NativeAdUnit(
    id: String,
    name: String,
    nativeAdTypes: NativeAdTypes,
    private val nativeAdCategory: NativeAdCategory,
) : AdUnit<NativeAd, NativeAdTypes>(id, name, nativeAdTypes) {

    suspend fun loadAd(
        context: Context
    ) {
        if (shouldLoadAd() || nativeAdCategory == NativeAdCategory.NATIVE_RECYCLERVIEW) {
            val adLoadedPair = loadNativeAd(
                context,
                id
            )
            val loadedAd = adLoadedPair.first
            if (loadedAd != null) {
                ad = loadedAd
                _statusFlow.value = AdStatus.Ready
                Log.d("AD-TAG", "Ad Status:$name ==> ${AdStatus.Ready} ")
            } else {
                _statusFlow.value = AdStatus.Failure
                Log.d("AD-TAG", "Ad Status:$name ==> ${AdStatus.Failure} ")
            }
        }
    }

    suspend fun loadNativeAd(
        context: Context,
        adId: String
    ): Pair<NativeAd?, LoadAdError?> = suspendCancellableCoroutine { continuation ->
        val adLoader = AdLoader.Builder(context, adId)
            .forNativeAd { nativeAd ->
                continuation.resume(nativeAd to null)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("AD-TAG", "Ad Status:$name ==> ${error.message} ")
                    continuation.resume(null to error)
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    if (nativeAdCategory == NativeAdCategory.NATIVE_REGULAR) {
                        _statusFlow.value = AdStatus.Shown
                    }
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        val adRequest = AdRequest.Builder().build()

        try {
            _statusFlow.value = AdStatus.Loading
            adLoader.loadAd(adRequest)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            if (continuation.isActive) {
                continuation.resume(
                    null to LoadAdError(
                        1,
                        e.message ?: "Invocation error",
                        "AdLoader",
                        null,
                        null
                    )
                )
            }
        }

        continuation.invokeOnCancellation {

        }
    }
}