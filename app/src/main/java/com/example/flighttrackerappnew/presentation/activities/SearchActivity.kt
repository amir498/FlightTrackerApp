package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivitySearchBinding
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.clickCount
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.setZoomClickEffect
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject

class SearchActivity : BaseActivity<ActivitySearchBinding>(ActivitySearchBinding::inflate) {
    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        viewListeners()
        loadAd()
    }

    override fun onResume() {
        super.onResume()
        loadInterstitialAd()
    }

    private fun loadInterstitialAd() {
        val INTERSTITIAL_SEARCH =
            RemoteConfigManager.getBoolean("INTERSTITIAL_SEARCH")
        if (INTERSTITIAL_SEARCH){
            InterstitialAdManager.loadInterstitialAd(
                this,
                app.getString(R.string.INTERSTITIAL_SEARCH),
                this,
                {},
                {},
                {})
        }
    }

    private fun loadAd() {
        val NATIVE_SEARCH_ACTIVITY =
            RemoteConfigManager.getBoolean("NATIVE_SEARCH_ACTIVITY")
        if (NATIVE_SEARCH_ACTIVITY){
            binding.flAdplaceholder.visible()
            app.let {
                nativeAdController.apply {
                    loadNativeAd(
                        this@SearchActivity, app.getString(R.string.NATIVE_SEARCH_ACTIVITY)
                    )
                    showNativeAd(this@SearchActivity, binding.flAdplaceholder)
                }
            }
        }

    }

    private fun viewListeners() {
        binding.apply {
            btnAirport.setZoomClickEffect()
            btnAirport.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@SearchActivity) {
                        startActivity(Intent(this@SearchActivity, SearchAirportActivity::class.java))
                    }
                } ?: run {
                    startActivity(Intent(this@SearchActivity, SearchAirportActivity::class.java))

                }
            }
            btnAirlines.setZoomClickEffect()
            btnAirlines.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@SearchActivity) {
                        startActivity(Intent(this@SearchActivity, SearchAirLinesActivity::class.java))

                    }
                } ?: run {
                    startActivity(Intent(this@SearchActivity, SearchAirLinesActivity::class.java))

                }
            }
            btnAircraft.setZoomClickEffect()
            btnAircraft.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@SearchActivity) {
                        startActivity(Intent(this@SearchActivity, SearchAircraftActivity::class.java))

                    }
                } ?: run {
                    startActivity(Intent(this@SearchActivity, SearchAircraftActivity::class.java))

                }
            }
            btnTailNumber.setZoomClickEffect()
            btnTailNumber.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@SearchActivity) {
                        startActivity(Intent(this@SearchActivity, SearchTailActivity::class.java))
                    }
                } ?: run {
                    startActivity(Intent(this@SearchActivity, SearchTailActivity::class.java))
                }
            }
            btnBack.setZoomClickEffect()
            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}