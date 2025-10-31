package com.example.flighttrackerappnew.presentation.admob.native

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ShimmerLayoutBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun <T> AppCompatActivity.collectLatestOnResume(
    flow: Flow<T>,
    action: suspend (value: T) -> Unit
): Job {
    return lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            flow.collectLatest(action)
        }
    }
}

fun AppCompatActivity.showNativeAd(
    showFakeLoading: Boolean,
    adGroup: NativeAdsManager,
    frameLayout: FrameLayout?,
    adLayout: Int
): Job? {
    if (frameLayout == null) return null
    val statusFlow = adGroup.nativeAdUnit.statusFlow
    return collectLatestOnResume(statusFlow) { status ->
        showNativeAdInner(
            showFakeLoading,
            status = status,
            layoutInflater = layoutInflater,
            adGroup = adGroup,
            frameLayout = frameLayout,
            adLayout = adLayout,
            adGroup.nativeAdUnit.adType
        )
    }
}

private fun showNativeAdInner(
    showFakeLoading: Boolean,
    status: AdStatus,
    layoutInflater: LayoutInflater,
    adGroup: NativeAdsManager,
    frameLayout: FrameLayout,
    adLayout: Int,
    adType: NativeAdTypes
) {
    try {
        when (status) {
            AdStatus.Shown -> {
                Log.d("akdna", adGroup.nativeAdUnit.name)
                Log.d("akdna", AdStatus.Shown.toString())
                Log.d("akdna", "______________________")
            }

            AdStatus.Failure -> {
                Log.d("akdna", adGroup.nativeAdUnit.name)
                Log.d("akdna", AdStatus.Failure.toString())
                Log.d("akdna", "______________________")
                frameLayout.visibility = View.GONE
            }

            AdStatus.Loading -> {
                Log.d("akdna", adGroup.nativeAdUnit.name)
                Log.d("akdna", AdStatus.Loading.toString())
                Log.d("akdna", "______________________")
                showLoading(frameLayout, layoutInflater, adLayout)
            }

            AdStatus.Ready -> {
                Log.d("akdna", adGroup.nativeAdUnit.name)
                Log.d("akdna", AdStatus.Ready.toString())
                Log.d("akdna", "______________________")

                val handler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    frameLayout.visibility = View.VISIBLE
                    val nativeAd = adGroup.nativeAdUnit.ad ?: return@Runnable
                    val adView = layoutInflater.inflate(adLayout, null) as NativeAdView

                    when (adType) {
                        NativeAdTypes.NATIVE_SMALL -> {
                            populateNativeAdViewSmall(nativeAd, adView)
                        }

                        NativeAdTypes.NATIVE_FULL -> {
                            populateNativeAdViewLarge(nativeAd, adView)
                        }

                        NativeAdTypes.NATIVE_LARGE -> {
                            populateNativeAdViewLarge(nativeAd, adView)
                        }
                    }

                    try {
                        frameLayout.removeAllViews()
                        (adView.parent as? ViewGroup)?.removeAllViews()
                        frameLayout.addView(adView)
                        frameLayout.post {
                            frameLayout.requestFocus()
                            frameLayout.requestLayout()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        frameLayout.visibility = View.GONE
                    }
                }
                if (showFakeLoading) {
                    showFakeLoading(frameLayout, layoutInflater, adLayout)
                    handler.postDelayed(runnable, 100)
                } else {
                    handler.postDelayed(runnable, 0)
                }
            }

            else -> {}
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        frameLayout.visibility = View.GONE
    }
}

private fun showLoading(frameLayout: FrameLayout, layoutInflater: LayoutInflater, adLayout: Int) {
    frameLayout.visibility = View.VISIBLE
    val shimmerBinding = ShimmerLayoutBinding.inflate(layoutInflater, null, false)
    shimmerBinding.root.addView(layoutInflater.inflate(adLayout, null))
    frameLayout.removeAllViews()
    frameLayout.addView(shimmerBinding.root)
}

private fun showFakeLoading(
    frameLayout: FrameLayout,
    layoutInflater: LayoutInflater,
    adLayout: Int
) {
    frameLayout.visibility = View.VISIBLE
    val shimmerBinding = ShimmerLayoutBinding.inflate(layoutInflater, null, false)
    shimmerBinding.root.addView(layoutInflater.inflate(adLayout, null))
    frameLayout.removeAllViews()
    frameLayout.addView(shimmerBinding.root)
}

private fun populateNativeAdViewSmall(nativeAd: NativeAd, adView: NativeAdView) {
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.mediaView = adView.findViewById(R.id.ad_media)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.starRatingView = adView.findViewById(R.id.starRating)
    adView.iconView = adView.findViewById(R.id.ad_app_icon)
    val adLabelView: TextView = adView.findViewById(R.id.adLabel)

    if (nativeAd.headline != null) {
        adView.headlineView?.visibility = View.VISIBLE
        (adView.headlineView as TextView).text = nativeAd.headline
    } else {
        adView.headlineView?.visibility = View.GONE
    }

    if (nativeAd.callToAction != null) {
        adView.callToActionView?.visibility = View.VISIBLE
        (adView.callToActionView as TextView).text = nativeAd.callToAction
    } else {
        adView.callToActionView?.visibility = View.GONE
    }

    if (nativeAd.mediaContent != null) {
        adView.mediaView?.visibility = View.VISIBLE
        adView.mediaView?.mediaContent = nativeAd.mediaContent
    } else {
        adView.mediaView?.visibility = View.GONE
    }

    if (nativeAd.body != null) {
        adView.bodyView?.visibility = View.VISIBLE
        (adView.bodyView as TextView).text = nativeAd.body
    } else {
        adView.bodyView?.visibility = View.GONE
    }

    if (nativeAd.starRating != null) {
        adView.starRatingView?.visibility = View.VISIBLE
        (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
    } else {
        adView.starRatingView?.visibility = View.GONE
    }

    if (nativeAd.icon != null) {
        adView.iconView?.visibility = View.VISIBLE
        (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
    } else {
        adView.iconView?.visibility = View.GONE
    }

    adLabelView.visibility = View.VISIBLE
    adView.setNativeAd(nativeAd)
    adView.setOnClickListener(null)
}

private fun populateNativeAdViewLarge(nativeAd: NativeAd, adView: NativeAdView) {
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.mediaView = adView.findViewById(R.id.ad_media)

    val adLabelView: TextView = adView.findViewById(R.id.adLabel)

    if (nativeAd.headline != null) {
        adView.headlineView?.visibility = View.VISIBLE
        (adView.headlineView as TextView).text = nativeAd.headline
    } else {
        adView.headlineView?.visibility = View.GONE
    }

    if (nativeAd.callToAction != null) {
        adView.callToActionView?.visibility = View.VISIBLE
        (adView.callToActionView as TextView).text = nativeAd.callToAction
    } else {
        adView.callToActionView?.visibility = View.GONE
    }

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
