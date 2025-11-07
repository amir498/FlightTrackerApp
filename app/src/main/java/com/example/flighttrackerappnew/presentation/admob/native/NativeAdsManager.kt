package com.example.flighttrackerappnew.presentation.admob.native

import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.canRequestAd
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NativeAdsManager(
    ids: Pair<String, String>,
    adType: NativeAdTypes,
    nativeAdCategory: NativeAdCategory = NativeAdCategory.NATIVE_REGULAR
) : KoinComponent {
    val nativeAdUnit: NativeAdUnit = NativeAdUnit(ids.first, ids.second, adType, nativeAdCategory)
    private val config: Config by inject()

    fun loadNativeAd(
        context: Context,
        isNativeEnabledFromRemote: Boolean
    ) {
        if (!config.isPremiumUser) {
            if (context.canRequestAd()) {
                loadAd(context, isNativeEnabledFromRemote)
            }
        }
    }

    fun loadAd(
        context: Context,
        isNativeEnabledFromRemote: Boolean
    ) {
            if (isNativeEnabledFromRemote && context.isNetworkAvailable()) {
                CoroutineScope(Dispatchers.IO).launch {
                    nativeAdUnit.loadAd(context)
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
        if (!config.isPremiumUser) {
            activity.showNativeAd(
                showFakeLoading,
                adGroup = adGroup,
                frameLayout = frameLayout,
                adLayout = adLayout
            )
        }
    }
}