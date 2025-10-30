package com.example.flighttrackerappnew.presentation.admob.banner

import android.app.Activity
import android.widget.FrameLayout
import com.example.flighttrackerappnew.presentation.admob.ump.UMPConsentManager
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.canRequestAd
import com.example.flighttrackerappnew.presentation.utils.initializeMobileAdsOnce
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BannerAdsManager(
    ids: Pair<String, String>,
    private val isBannerEnabled: Boolean,
    adType: BannerAdTypes,
) : KoinComponent {
    val bannerAdUnit: BannerAdUnit = BannerAdUnit(ids.first, ids.second, adType)
    private val config: Config by inject()

    fun loadAndShowBannerAd(
        context: Activity,
        adContainerView: FrameLayout,
        onStartLoadingAd: () -> Unit
    ) {
        if (!config.isPremiumUser) {
            if (context.canRequestAd()) {
                loadAd(
                    context,
                    adContainerView,
                    onStartLoadingAd
                )
            } else {
                UMPConsentManager(context).apply {
                    checkConsent { consentObtained ->
                        if (consentObtained) {
                            if (context.canRequestAd()) {
                                loadAd(
                                    context,
                                    adContainerView,
                                    onStartLoadingAd
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun loadAd(
        context: Activity,
        adContainerView: FrameLayout,
        onStartLoadingAd: () -> Unit
    ) {
        context.initializeMobileAdsOnce {
            if (isBannerEnabled && context.isNetworkAvailable()) {
                CoroutineScope(Dispatchers.IO).launch {
                    bannerAdUnit.loadAd(context, adContainerView, onStartLoadingAd)
                }
            }
        }
    }
}