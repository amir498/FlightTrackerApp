package com.example.flighttrackerappnew.presentation.adManager.rewarded

import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
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

    private fun showDialogForAd(activity: Activity): Dialog {
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
            return
        }

        val consentInfo = UserMessagingPlatform.getConsentInformation(activity)
        if (!consentInfo.canRequestAds()) {
            return
        }

        val dialog = showDialogForAd(activity)
        Handler(Looper.getMainLooper()).postDelayed({
            showRewardAd(activity, adUnitId, onRewardEarned, onAdFailed) {
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
                function.invoke()
                onAdFailed()
            }

            override fun onAdLoaded(ad: RewardedAd) {

                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        if (rewardEarned) {
                            onRewardEarned()
                            rewardEarned = false
                        }
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        rewardEarned = false
                        function.invoke()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Handler(Looper.getMainLooper()).postDelayed({
                            function.invoke()
                        }, 1000)
                    }

                    override fun onAdClicked() {}

                    override fun onAdImpression() {}
                }

                ad.show(activity) {
                    rewardEarned = true
                }
            }
        })
    }
}