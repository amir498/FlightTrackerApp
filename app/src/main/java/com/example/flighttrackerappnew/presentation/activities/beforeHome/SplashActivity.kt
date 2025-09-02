package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.example.flighttrackerappnew.FlightApp.Companion.canRequestAd
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivitySplashBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.MainActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager.showAd
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.ump.UMPConsentManager
import com.example.flighttrackerappnew.presentation.utils.getCurrentCountryLatLon
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.lat
import com.example.flighttrackerappnew.presentation.utils.lon
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
    private var adLoaded: Boolean = false
    private var runAd: Boolean = true
    private val viewModel: FlightAppViewModel by inject()
    private val bannerAdManager: BannerAdManager by inject()
    private val nativeAdController: NativeAdController by inject()
    private val runnable2 = Runnable {
        if (!adLoaded) {
            runAd = false
            navigateNext()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val runnable = Runnable {
            binding.apply {
                liveFlightTrack.visible()
                liveFlightTrack2.visible()
            }
        }
        handler.postDelayed(runnable, 1000)

        if (!isNetworkAvailable()) {
            adLoaded = true
            showDialog()
        }

        handler.postDelayed(runnable2, 9000)
    }

    private fun getLongLatFirst() {
        val pair = getCurrentCountryLatLon(this)
        lat = pair?.first
        lon = pair?.second
        lat?.let { lon?.let { it1 -> getAllApiCall(it, it1) } }
    }

    private fun navigateNext() {
        if (config.isPrivacyPolicyAccepted) {
            if (config.isPremiumUser) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                val intent = Intent(this, PremiumActivity::class.java)
                intent.putExtra("from_splash", true)
                startActivity(intent)
            }
        } else {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable() && !config.isPremiumUser) {
            getLongLatFirst()
            umpConsentForm()
        }
    }

    fun getAllApiCall(lat: Double, lon: Double) {
        Log.d("MY----TAG", "getAllApiCall")
        val distance =
            RemoteConfigManager.getString("distance")
        viewModel.getAllData(lat, lon, distance.toInt()) {
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

                                        if (showNative1Lang1 && !config.isPremiumUser) {
                                            nativeAdController.loadLanguageScreen1NativeAd1(
                                                this@SplashActivity,
                                                app.getString(R.string.NATIVE1_LANGUAGESCREEN1)
                                            )
                                        }

                                        if (showBanner && !config.isPremiumUser) {
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
                                                }, {
                                                })
                                        }
                                        if (showInt && !config.isPremiumUser) {
                                            InterstitialAdManager.loadInterstitialAd(
                                                this@SplashActivity,
                                                app.getString(R.string.INTERSTITIAL_SPLASH),
                                                {
                                                    adLoaded = true
                                                    if (runAd) {
                                                        handler.postDelayed({
                                                            showAd(this@SplashActivity, null)
                                                        }, 1000)
                                                    }
                                                },
                                                {
                                                    adLoaded = true
                                                    if (runAd) {
                                                        if (config.isPrivacyPolicyAccepted) {
                                                            if (config.isPremiumUser) {
                                                                startActivity(
                                                                    Intent(
                                                                        this@SplashActivity,
                                                                        MainActivity::class.java
                                                                    )
                                                                )
                                                            } else {
                                                                val intent = Intent(
                                                                    this@SplashActivity,
                                                                    PremiumActivity::class.java
                                                                )
                                                                intent.putExtra("from_splash", true)
                                                                startActivity(
                                                                    intent
                                                                )
                                                            }
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
                                                },
                                                {
                                                    adLoaded = true
                                                    if (runAd) {
                                                        if (config.isPrivacyPolicyAccepted) {
                                                            if (config.isPremiumUser) {
                                                                startActivity(
                                                                    Intent(
                                                                        this@SplashActivity,
                                                                        MainActivity::class.java
                                                                    )
                                                                )
                                                            } else {
                                                                val intent = Intent(
                                                                    this@SplashActivity,
                                                                    PremiumActivity::class.java
                                                                )
                                                                intent.putExtra("from_splash", true)
                                                                startActivity(intent)
                                                            }
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
                                            )
                                        } else {
                                            if (runAd) {
                                                adLoaded = true
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
                    } else {
                        if (runAd) {
                            adLoaded = true
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
    }

    private fun checkInternetConnection() {
        if (isNetworkAvailable()) {
            getLongLatFirst()
            if (!config.isPremiumUser) {
                umpConsentForm()
            }
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