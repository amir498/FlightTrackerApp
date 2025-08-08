package com.example.flighttrackerappnew.presentation.adManager.rewarded

import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.rewardEarned
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.ump.UserMessagingPlatform

class RewardedAdManager {

    private var rewardedAd: RewardedAd? = null
    private val TAG = "RewardedAdManager"

    fun showDialogForAd(activity: Activity): Dialog {
        return CustomDialogBuilder(activity)
            .setLayout(R.layout.dialog_ad_loading)
            .setCancelable(true)
            .setPositiveClickListener {
            }.setNegativeClickListener {

            }.show(true)
    }

    fun loadAndShowRewardedAd(
        activity: Activity,
        adUnitId: String,
        onRewardEarned: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        if (!activity.isNetworkAvailable()) {
            Log.d(TAG, "No internet connection.")
            return
        }

        val consentInfo = UserMessagingPlatform.getConsentInformation(activity)
        if (!consentInfo.canRequestAds()) {
            Log.d(TAG, "Cannot request ads due to consent.")
            return
        }

        val dialog = showDialogForAd(activity)
        Handler(Looper.getMainLooper()).postDelayed({
            showRewardAd(activity, adUnitId, onRewardEarned, onAdFailed){
                dialog.dismiss()
            }
        }, 1000)


    }

    private fun showRewardAd(
        activity: Activity,
        adUnitId: String,
        onRewardEarned: () -> Unit,
        onAdFailed: () -> Unit,
        function: () -> Unit
    ) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, adUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Ad failed to load: ${adError.message}")
                onAdFailed()
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad

                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        if (rewardEarned) {
                            onRewardEarned()
                            rewardEarned = false
                        }
                        rewardedAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        rewardEarned = false
                        Log.e(TAG, "Ad failed to show: ${adError.message}")
                        rewardedAd = null
                        function.invoke()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen.")
                        function.invoke()
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Ad clicked.")
                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "Ad impression logged.")
                    }
                }

                ad.show(activity) {
                    rewardEarned = true
                }
            }
        })
    }
}