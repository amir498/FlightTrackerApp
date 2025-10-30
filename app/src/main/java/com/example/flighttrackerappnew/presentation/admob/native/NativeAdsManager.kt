package com.example.flighttrackerappnew.presentation.admob.native

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.flighttrackerappnew.presentation.admob.ump.UMPConsentManager
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.canRequestAd
import com.example.flighttrackerappnew.presentation.utils.initializeMobileAdsOnce
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.runWithDelay
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NativeAdsManager(
    ids: Pair<String, String>,
    adType: NativeAdTypes,
    nativeAdCategory: NativeAdCategory = NativeAdCategory.NATIVE_REGULAR
): KoinComponent {
    val nativeAdUnit: NativeAdUnit = NativeAdUnit(ids.first, ids.second, adType,nativeAdCategory)
    private val config: Config by inject()

    fun loadNativeAd(
        context: Context,
        isNativeEnabledFromRemote: Boolean
    ) {
        if (!config.isPremiumUser) {
            if (context.canRequestAd()) {
                loadAd(context, isNativeEnabledFromRemote)
            } else {
                UMPConsentManager(context as Activity).apply {
                    checkConsent { consentObtained ->
                        if (consentObtained) {
                            if (context.canRequestAd()) {
                                loadAd(context, isNativeEnabledFromRemote)
                            }
                        }
                    }
                }
            }
        }
    }

    fun loadAd(
        context: Context,
        isNativeEnabledFromRemote: Boolean
    ) {
        (context as Activity).initializeMobileAdsOnce {
            if (isNativeEnabledFromRemote && context.isNetworkAvailable()) {
                CoroutineScope(Dispatchers.IO).launch {
                    nativeAdUnit.loadAd(context)
                }
            }
        }
    }

    fun showNativeAd(
        showFakeLoading: Boolean = false,
        adGroup: NativeAdsManager,
        frameLayout: FrameLayout?,
        adLayout: Int,
        activity: AppCompatActivity,
    ) {
        runWithDelay(delay = 0) {
            activity.showNativeAd(
                showFakeLoading,
                adGroup = adGroup,
                frameLayout = frameLayout,
                adLayout = adLayout
            )
        }
    }

    fun getLoadedAd(): NativeAd? {
        return nativeAdUnit.ad
    }
}