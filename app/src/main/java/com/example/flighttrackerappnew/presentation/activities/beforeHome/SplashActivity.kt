package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivitySplashBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_SPLASH
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.DEFAULT_DISTANCE
import com.example.flighttrackerappnew.presentation.utils.getCurrentCountryLatLon
import com.example.flighttrackerappnew.presentation.utils.isNetworkAvailable
import com.example.flighttrackerappnew.presentation.utils.lat
import com.example.flighttrackerappnew.presentation.utils.lon
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    private var adLoaded: Boolean = false
    private val viewModel: FlightAppViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isNetworkAvailable()) {
            adLoaded = true
            showDialog()
        } else {
            loadAd()
        }
    }

    private fun loadAd() {
        if (config.isPremiumUser) {
            if (config.isPrivacyPolicyAccepted) {
                val intent = Intent(this@SplashActivity, LanguageActivity::class.java)
                intent.putExtra("fromSetting", false)
                startActivity(intent)
            } else {
                startActivity(Intent(this, PrivacyPolicyActivity::class.java))
            }
        } else {
            InterstitialAdManager.mInterstitialAd = null
            InterstitialAdManager.loadInterstitialAd(
                ignoreClickCount = true,
                showLoadingScreenWithDelay = RemoteConfigManager.getNumber("showLoadingScreenWithDelay"),
                showLoadingAsLoadAdRequestCalled = false,
                interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                showWhenReady = true,
                activity = this@SplashActivity,
                adUnitId = app.getString(R.string.INTERSTITIAL_SPLASH),
                isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_SPLASH"),
                adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                {
                    if (config.isPrivacyPolicyAccepted) {
                        val intent = Intent(this@SplashActivity, PremiumActivity::class.java)
                        intent.putExtra("fromSetting", false)
                        intent.putExtra("from_splash", true)
                        startActivity(intent)
                    } else {
                        startActivity(Intent(this, PrivacyPolicyActivity::class.java))
                    }
                    finish()
                }, {
                    binding.apply {
                        loadingText.visible()
                        lottiepg.visible()
                    }
                })

            BANNER_SPLASH.apply {
                loadAndShowBannerAd(
                    context = this@SplashActivity,
                    adContainerView = binding.adContainerView,
                    onStartLoadingAd = {}
                )
            }

            NativeAdProvider.native_1_LANGUAGE_SCREEN1.loadNativeAd(
                this,
                RemoteConfigManager.getBoolean("native_1_LANGUAGE_SCREEN1")
            )
        }
    }

    private fun getLongLatFirst() {
        val pair = getCurrentCountryLatLon(this)
        lat = pair?.first
        lon = pair?.second
        lat?.let { lon?.let { it1 -> getAllApiCall(it, it1) } }
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable() && !config.isPremiumUser) {
            getLongLatFirst()
        }
    }

    fun getAllApiCall(lat: Double, lon: Double) {
        val distanceStr = RemoteConfigManager.getString("distance")
        val distance = distanceStr.toIntOrNull() ?: DEFAULT_DISTANCE

        viewModel.getAllData(lat, lon, distance) {
        }
    }

    private fun checkInternetConnection() {
        if (isNetworkAvailable()) {
            getLongLatFirst()
            if (!config.isPremiumUser) {
                loadAd()
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