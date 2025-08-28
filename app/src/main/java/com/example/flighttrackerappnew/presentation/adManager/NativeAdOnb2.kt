package com.example.flighttrackerappnew.presentation.adManager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
import com.google.android.gms.ads.nativead.NativeAdView
import java.lang.reflect.InvocationTargetException
import kotlin.let
import kotlin.run

class NativeAdOnb2 {
    private lateinit var adLoader: AdLoader
    private var nativeLanguageScreenAd: NativeAd? = null
    private lateinit var adPlaceholder: FrameLayout
    var fn: (() -> Unit)? = null

    fun loadCall(context: Context,nativeLanguage:String) {
        loadNativeAd(context,nativeLanguage)
    }

    fun showCall(
        context: Context,
        frameLayout: FrameLayout,
    ) {
        showNativeAd(frameLayout, context)
    }

    private fun loadNativeAd(context: Context,nativeLanguage:String) {
        adLoader = AdLoader.Builder(context, nativeLanguage).forNativeAd { nativeAd ->
            nativeLanguageScreenAd = nativeAd
        }.withAdListener(object : AdListener() {
            override fun onAdLoaded() {
                fn?.let {
                    it()
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
            }

            override fun onAdClicked() {}

            override fun onAdImpression() {}
        })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()
        val adRequest = AdRequest.Builder().build()

        try {
            adLoader.loadAd(adRequest)
        }catch (e: InvocationTargetException){
            e.printStackTrace()
        }
    }

    private fun showNativeAd(
        frameLayout: FrameLayout,
        context: Context
    ) {
        if (context.isNetworkAvailable()) {
            this.adPlaceholder = frameLayout
            nativeLanguageScreenAd?.let {
                fn = null
                layoutInflaterForAd(context)
            } ?: run {
                fn = { layoutInflaterForAd(context) }
            }
        }
    }

    private fun populateNativeAdViewWithMedia(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
//        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.mediaView = adView.findViewById(R.id.ad_media)
//        adView.iconView = adView.findViewById(R.id.ad_app_icon)

        val adLabelView: TextView = adView.findViewById(R.id.adLabel)

        if (nativeAd.headline != null) {
            adView.headlineView?.visibility = View.VISIBLE
            (adView.headlineView as TextView).text = nativeAd.headline
        } else {
            adView.headlineView?.visibility = View.GONE
        }

        if (nativeAd.body != null) {
//            adView.bodyView?.visibility = View.VISIBLE
//            (adView.bodyView as TextView).text = nativeAd.body
        } else {
//            adView.bodyView?.visibility = View.GONE
        }

        if (nativeAd.callToAction != null) {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        } else {
            adView.callToActionView?.visibility = View.GONE
        }

//        if (nativeAd.icon != null) {
//            adView.iconView?.visibility = View.VISIBLE
//            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
//        } else {
//            adView.iconView?.visibility = View.GONE
//        }

        if (nativeAd.mediaContent != null) {
            adView.mediaView?.visibility = View.VISIBLE
            adView.mediaView?.mediaContent = nativeAd.mediaContent
        } else {
            adView.mediaView?.visibility = View.GONE
        }

        adLabelView.visibility = View.VISIBLE
        adView.setNativeAd(nativeAd)
        adView.setOnClickListener(null)
    }

    private fun layoutInflaterForAd(requireContext: Context) {
        val adView = LayoutInflater.from(requireContext)
            .inflate(R.layout.native_ad_layout_view_with_media, null) as NativeAdView
        populateNativeAdViewWithMedia(nativeLanguageScreenAd!!, adView)

        adPlaceholder.removeAllViews()
        adPlaceholder.addView(adView)
    }
}