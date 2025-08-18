package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.example.flighttrackerappnew.FlightApp.Companion.canRequestAd
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivitySplashBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.PrivacyPolicyActivity
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager.showAd
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.ump.UMPConsentManager
import com.example.flighttrackerappnew.presentation.utils.allApiCallCompleted
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    private val handler = Handler(Looper.getMainLooper())
    private val viewModel: FlightAppViewModel by inject()
    private val bannerAdManager: BannerAdManager by inject()
    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val runnable = Runnable {
            binding.liveFlightTrack.visible()
            binding.liveFlightTrack2.visible()
        }
        handler.postDelayed(runnable, 1000)

        if (isNetworkAvailable()) {
            getAllApiCall()
        } else {
            showDialog()
        }

    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()) {
            umpConsentForm()
        }
    }

    fun getAllApiCall() {
        allApiCallCompleted.postValue(false)
        viewModel.getAllData {
            allApiCallCompleted.postValue(true)
        }
    }

    private fun preloadLottie() {
        LottieCompositionFactory.fromRawRes(this, R.raw.splash)
            .addListener { composition ->
                val drawable = LottieDrawable()
                drawable.composition = composition
                drawable.repeatCount = LottieDrawable.INFINITE
                drawable.playAnimation()
                binding.ivSplash.setImageDrawable(drawable)
            }
            .addFailureListener { error ->
                error.printStackTrace()
            }
    }

    private fun umpConsentForm() {
        UMPConsentManager(this).apply {
            checkConsent { consentObtained ->
                if (consentObtained) {
                    val consentInformation =
                        UserMessagingPlatform.getConsentInformation(this@SplashActivity)
                    canRequestAd = consentInformation.canRequestAds()
                    if (canRequestAd) {
                        val backgroundScope = CoroutineScope(Dispatchers.IO)
                        backgroundScope.launch {
                            MobileAds.initialize(this@SplashActivity) {
                                RemoteConfigManager.remoteConfig.fetchAndActivate()
                                    .addOnCompleteListener { task ->
//                                        startActivity(
//                                            Intent(
//                                                this@SplashActivity,
//                                                MainActivity::class.java
//                                            )
//                                        )
//                                        return@addOnCompleteListener
                                        val showInt =
                                            RemoteConfigManager.getBoolean("INTERSTITIAL_SPLASH")
                                        val showBanner =
                                            RemoteConfigManager.getBoolean("BANNER_SPLASH")

                                        val showNative1Lang1 =
                                            RemoteConfigManager.getBoolean("NATIVE1_LANGUAGESCREEN1")

                                        if (showNative1Lang1) {
                                            nativeAdController.loadLanguageScreen1NativeAd1(
                                                this@SplashActivity,
                                                app.getString(R.string.NATIVE1_LANGUAGESCREEN1)
                                            )
                                        }

                                        if (showBanner) {
                                            binding.apply {
                                                loadingText.visible()
                                                adContainerView.visible()
                                            }
                                            bannerAdManager.loadAd(
                                                isCollapsible = false,
                                                this@SplashActivity,
                                                app.getString(R.string.BANNER_SPLASH), {
                                                    bannerAdManager.showBannerAd(
                                                        binding.adContainerView,
                                                        this@SplashActivity,
                                                        binding.loadingText
                                                    )
                                                    binding.loadingText.invisible()
                                                }, {
                                                    binding.loadingText.invisible()
                                                })
                                        }
                                        if (showInt) {
                                            InterstitialAdManager.loadInterstitialAd(
                                                this@SplashActivity,
                                                app.getString(R.string.INTERSTITIAL_SPLASH),
                                                this@SplashActivity,
                                                {
                                                    handler.postDelayed({
                                                        showAd(this@SplashActivity, null)
                                                    }, 1000)
                                                },
                                                {
                                                    if (config.isPrivacyPolicyAccepted) {
                                                        startActivity(
                                                            Intent(
                                                                this@SplashActivity,
                                                                LanguageActivity::class.java
                                                            )
                                                        )
                                                    } else {
                                                        startActivity(
                                                            Intent(
                                                                this@SplashActivity,
                                                                PrivacyPolicyActivity::class.java
                                                            )
                                                        )
                                                    }
                                                    finish()
                                                },
                                                {
                                                    if (config.isPrivacyPolicyAccepted) {
                                                        startActivity(
                                                            Intent(
                                                                this@SplashActivity,
                                                                LanguageActivity::class.java
                                                            )
                                                        )
                                                    } else {
                                                        startActivity(
                                                            Intent(
                                                                this@SplashActivity,
                                                                PrivacyPolicyActivity::class.java
                                                            )
                                                        )
                                                    }
                                                    finish()
                                                }
                                            )
                                        } else {
                                            if (config.isPrivacyPolicyAccepted) {
                                                startActivity(
                                                    Intent(
                                                        this@SplashActivity,
                                                        LanguageActivity::class.java
                                                    )
                                                )
                                            } else {
                                                startActivity(
                                                    Intent(
                                                        this@SplashActivity,
                                                        PrivacyPolicyActivity::class.java
                                                    )
                                                )
                                            }
                                            finish()
                                        }
                                    }
                            }
                        }
                    } else {
                        if (config.isPrivacyPolicyAccepted) {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    LanguageActivity::class.java
                                )
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    PrivacyPolicyActivity::class.java
                                )
                            )
                        }
                        finish()
                    }
                }
            }
        }
    }

    private fun checkInternetConnection() {
        if (isNetworkAvailable()) {
            getAllApiCall()
            umpConsentForm()
        } else {
            showDialog()
        }
    }

    private fun showDialog() {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_no_internet)
            .setCancelable(false)
            .setPositiveClickListener {
                it.dismiss()
                checkInternetConnection()
            }.setNegativeClickListener {
                it.dismiss()
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }.show()
    }
}