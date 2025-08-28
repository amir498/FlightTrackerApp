package com.example.flighttrackerappnew.presentation.adManager.controller

import android.content.Context
import android.widget.FrameLayout
import com.example.flighttrackerappnew.presentation.adManager.NativeAd1LangScreen1
import com.example.flighttrackerappnew.presentation.adManager.NativeAd1LangScreen2
import com.example.flighttrackerappnew.presentation.adManager.NativeAd2LangScreen1
import com.example.flighttrackerappnew.presentation.adManager.NativeAd2LangScreen2
import com.example.flighttrackerappnew.presentation.adManager.NativeAdHome
import com.example.flighttrackerappnew.presentation.adManager.NativeAdMapStyle
import com.example.flighttrackerappnew.presentation.adManager.NativeAdOnb1
import com.example.flighttrackerappnew.presentation.adManager.NativeAdOnb2
import com.example.flighttrackerappnew.presentation.adManager.NativeAdOnb4
import com.example.flighttrackerappnew.presentation.adManager.NativeAdOther
import com.example.flighttrackerappnew.presentation.adManager.NativeAdWelcomeScreen
import com.example.flighttrackerappnew.presentation.adManager.OnBoardingFullNativeAd1
import com.example.flighttrackerappnew.presentation.adManager.OnBoardingFullNativeAd2
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.ump.UserMessagingPlatform

class NativeAdController(
    private val nativeAd1LangScreen1: NativeAd1LangScreen1,
    private val nativeAd2LangScreen1: NativeAd2LangScreen1,
    private val nativeAd1LangScreen2: NativeAd1LangScreen2,
    private val nativeAd2LangScreen2: NativeAd2LangScreen2,
    private val nativeAdOnb1: NativeAdOnb1,
    private val nativeAdOnb2: NativeAdOnb2,
    private val nativeAdOnb4: NativeAdOnb4,
    private val nativeAdWelcomeScreen: NativeAdWelcomeScreen,
    private val nativeAdMapStyle: NativeAdMapStyle,
    private val onBoardingFullNativeAd1: OnBoardingFullNativeAd1,
    private val onBoardingFullNativeAd2: OnBoardingFullNativeAd2,
    private val nativeAdOther: NativeAdOther,
    private val nativeAdHome: NativeAdHome,
) {
    fun loadLanguageScreen1NativeAd1(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAd1LangScreen1.loadCall(context, adId)
        }
    }

    fun showLanguageScreen1NativeAd1(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAd1LangScreen1.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadLanguageScreen1NativeAd2(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAd2LangScreen1.loadCall(context, adId)
        }
    }

    fun showLanguageScreen1NativeAd2(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAd2LangScreen1.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadLanguageScreen2NativeAd1(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAd1LangScreen2.loadCall(context, adId)
        }
    }

    fun showLanguageScreen2NativeAd1(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAd1LangScreen2.showCall(
                context,
                frameLayout,
            )
        }
    }


    fun loadLanguageScreen2NativeAd2(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAd2LangScreen2.loadCall(context, adId)
        }
    }

    fun showLanguageScreen2NativeAd2(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAd2LangScreen2.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadOnb1NativeAd(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdOnb1.loadCall(context, adId)
        }
    }

    fun showOnb1NativeAd(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdOnb1.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadOnb4NativeAd(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdOnb4.loadCall(context, adId)
        }
    }

    fun showOnb4NativeAd(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdOnb4.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadOnb2NativeAd(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdOnb2.loadCall(context, adId)
        }
    }

    fun showOnb2NativeAd(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdOnb2.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadWelcomeScreenNativeAd(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdWelcomeScreen.loadCall(context, adId)
        }
    }

    fun showWelcomeScreenNativeAd(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdWelcomeScreen.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadMapStyleNativeAd(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdMapStyle.loadCall(context, adId)
        }
    }

    fun showMapStyleNativeAd(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdMapStyle.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadFullNativeAd1(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            onBoardingFullNativeAd1.loadCall(context, adId)
        }
    }

    fun showFullNativeAd1(
        flAdplaceholder: FrameLayout,
        nativeAdShimmer: ShimmerFrameLayout,
        requireContext: Context,
        function: () -> Unit
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(requireContext)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            onBoardingFullNativeAd1.showCall(
                flAdplaceholder,
                nativeAdShimmer,
                requireContext,
                function
            )
        }
    }

    fun loadFullNativeAd2(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            onBoardingFullNativeAd2.loadCall(context, adId)
        }
    }

    fun showFullNativeAd2(
        flAdplaceholder: FrameLayout,
        nativeAdShimmer: ShimmerFrameLayout,
        requireContext: Context,
        function: () -> Unit
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(requireContext)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            onBoardingFullNativeAd2.showCall(
                flAdplaceholder,
                nativeAdShimmer,
                requireContext,
                function
            )
        }
    }

    fun loadNativeAd(context: Context, adId: String) {
            val consentInformation = UserMessagingPlatform.getConsentInformation(context)
            val canRequestAds = consentInformation.canRequestAds()
            if (canRequestAds) {
                nativeAdOther.loadCall(context, adId)
            }
    }

    fun showNativeAd(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdOther.showCall(
                context,
                frameLayout,
            )
        }
    }

    fun loadNativeAdHome(context: Context, adId: String) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdHome.loadCall(context, adId)
        }
    }

    fun showNativeAdHome(
        context: Context,
        frameLayout: FrameLayout
    ) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        val canRequestAds = consentInformation.canRequestAds()
        if (canRequestAds) {
            nativeAdHome.showCall(
                context,
                frameLayout,
            )
        }
    }
}