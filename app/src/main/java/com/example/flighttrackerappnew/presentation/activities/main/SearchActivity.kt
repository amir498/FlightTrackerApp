package com.example.flighttrackerappnew.presentation.activities.main

import android.content.Intent
import android.os.Bundle
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivitySearchBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.SearchAirLinesActivity
import com.example.flighttrackerappnew.presentation.activities.SearchAircraftActivity
import com.example.flighttrackerappnew.presentation.activities.SearchAirportActivity
import com.example.flighttrackerappnew.presentation.activities.SearchTailActivity
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager.loadInterstitialAd
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_SEARCH_ACTIVITY
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.clickCount

class SearchActivity : BaseActivity<ActivitySearchBinding>(ActivitySearchBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewListeners()
        loadAd()
    }

    private fun loadAd() {
        NATIVE_SEARCH_ACTIVITY.apply {
            loadNativeAd(
                this@SearchActivity,
                RemoteConfigManager.getBoolean("NATIVE_SEARCH_ACTIVITY")
            )
            showNativeAd(
                adGroup = NATIVE_SEARCH_ACTIVITY,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_layout_view_with_media,
                activity = this@SearchActivity
            )
        }
    }

    private fun viewListeners() {
        binding.apply {
            btnAirport.setOnClickListener {
                clickCount += 1
                loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@SearchActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_SEARCH),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_SEARCH"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                SearchAirportActivity::class.java
                            )
                        )
                    }, {

                    })
            }
            btnAirlines.setOnClickListener {
                clickCount += 1
                loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@SearchActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_SEARCH),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_SEARCH"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                SearchAirLinesActivity::class.java
                            )
                        )
                    }, {

                    })
            }
            btnAircraft.setOnClickListener {
                clickCount += 1
                loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@SearchActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_SEARCH),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_SEARCH"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                SearchAircraftActivity::class.java
                            )
                        )
                    }, {

                    })
            }
            btnTailNumber.setOnClickListener {
                clickCount += 1
                loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@SearchActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_SEARCH),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_SEARCH"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(Intent(this@SearchActivity, SearchTailActivity::class.java))

                    }, {

                    })
            }
            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}