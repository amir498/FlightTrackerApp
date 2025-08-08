package com.example.flighttrackerappnew.presentation.adManager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaAspectRatio
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
import com.google.android.gms.ads.nativead.NativeAdView

class OnBoardingFullNativeAd2 {

    private var nativeAdFull: NativeAd? = null
    private lateinit var adPlaceholder: FrameLayout
    private lateinit var shimmerLayout: ShimmerFrameLayout

    fun loadCall(context: Context, adId: String) {
        loadFullNativeAd(context, adId)
    }

    fun showCall(
        frameLayout: FrameLayout,
        shimmerLayout: ShimmerFrameLayout,
        context: Context,
        function: () -> Unit
    ) {
        showNativeAd(
            frameLayout,
            shimmerLayout,
            context,
            function
        )
    }

    private fun showNativeAd(
        frameLayout: FrameLayout,
        shimmerLayout: ShimmerFrameLayout,
        context: Context,
        function: () -> Unit
    ) {
        if (context.isNetworkAvailable()) {
            this.adPlaceholder = frameLayout
            this.shimmerLayout = shimmerLayout
            nativeAdFull?.let {
                layoutInflaterFull(context)
            } ?: run {
                function()
            }
        } else {
            function()
            shimmerLayout.gone()
        }
    }

    private fun loadFullNativeAd(context: Context, adId: String) {
        if (context.isNetworkAvailable()) {
            val adOptions = NativeAdOptions.Builder()
                .setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
                .build()

            val adLoader = AdLoader.Builder(context, adId)
                .withNativeAdOptions(adOptions)
                .forNativeAd { nativeAd ->
                    nativeAdFull = nativeAd
                }
                .withAdListener(object : AdListener() {
                    override fun onAdLoaded() {

                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        context.showToast("Failed To Load")
                    }

                    override fun onAdClicked() {}

                    override fun onAdImpression() {}
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setAdChoicesPlacement(ADCHOICES_TOP_RIGHT)
                        .build()
                ).build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    private fun layoutInflaterFull(requireContext: Context) {
        shimmerLayout.invisible()
        val adView: NativeAdView = LayoutInflater.from(requireContext)
            .inflate(R.layout.full_screen_native_ad_view, null) as NativeAdView
        populateFullScreenNativeAd(adView, nativeAdFull!!)
        adPlaceholder.removeAllViews()
        adPlaceholder.addView(adView)
    }

    private fun populateFullScreenNativeAd(adView: NativeAdView, nativeAd: NativeAd) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)

        val adLabelView: TextView = adView.findViewById(R.id.adLabel)

        if (nativeAd.headline != null) {
            adView.headlineView?.visibility = View.VISIBLE
            (adView.headlineView as TextView).text = nativeAd.headline
        } else {
            adView.headlineView?.visibility = View.GONE
        }

        if (nativeAd.body != null) {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        } else {
            adView.bodyView?.visibility = View.GONE
        }

        if (nativeAd.callToAction != null) {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        } else {
            adView.callToActionView?.visibility = View.GONE
        }

        if (nativeAd.icon != null) {
            adView.iconView?.visibility = View.VISIBLE
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
        } else {
            adView.iconView?.visibility = View.GONE
        }

        if (nativeAd.mediaContent != null) {
            adView.mediaView?.mediaContent = nativeAd.mediaContent
        }

        adLabelView.visibility = View.VISIBLE
        adView.setNativeAd(nativeAd)
    }
}