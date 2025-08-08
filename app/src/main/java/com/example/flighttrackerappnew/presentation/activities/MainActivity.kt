package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityMainBinding
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.allApiCallCompleted
import com.example.flighttrackerappnew.presentation.utils.clickCount
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isFromDetail
import com.example.flighttrackerappnew.presentation.utils.lastSelectedPlane
import com.example.flighttrackerappnew.presentation.utils.setZoomClickEffect
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val bannerAdManager: BannerAdManager by inject()
    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val BANNER_HOME =
            RemoteConfigManager.getBoolean("BANNER_HOME")

        val NATIVE_HOME =
            RemoteConfigManager.getBoolean("NATIVE_HOME")

        binding.subMain.setPadding(
            0,
            getStatusBarHeight,
            0,
            0
        )

        viewListener()
        observeLiveData()
        onBackPress()

        if (BANNER_HOME) {
            binding.flAdplaceholder.visible()
            bannerAdManager.loadAd(true, this, app.getString(R.string.BANNER_HOME), {
                bannerAdManager.showBannerAd(
                    binding.adContainerView,
                    this@MainActivity,
                    null
                )
            }, {})
        }
        if (NATIVE_HOME) {
            binding.adContainerView.visible()
            loadAd()
        }

    }

    private fun loadInterstitialAd() {
        val INTERSTITIAL_HOME =
            RemoteConfigManager.getBoolean("INTERSTITIAL_HOME")
        if (INTERSTITIAL_HOME){
            InterstitialAdManager.loadInterstitialAd(
                this,
                app.getString(R.string.INTERSTITIAL_SPLASH),
                this,
                {},
                {},
                {})
        }
    }

    private fun loadAd() {
        binding.flAdplaceholder.visible()
        app.let {
            nativeAdController.apply {
                loadNativeAdHome(
                    this@MainActivity, app.getString(R.string.NATIVE_HOME)
                )
                showNativeAdHome(this@MainActivity, binding.flAdplaceholder)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadInterstitialAd()
        lastSelectedPlane = null
        isFromDetail = false
    }

    private fun onBackPress() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    private fun showExitDialog() {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_exit_app)
            .setCancelable(false)
            .setPositiveClickListener {
                it.dismiss()
                job?.cancel()
                finish()
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    private fun viewListener() {
        binding.apply {
            viewMapBtn.setZoomClickEffect()
            viewMapBtn.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@MainActivity) {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                LiveMapFlightTrackerActivity::class.java
                            )
                        )
                    }
                } ?: run {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            LiveMapFlightTrackerActivity::class.java
                        )
                    )
                }
            }
            btnSetting.setZoomClickEffect()
            btnSetting.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@MainActivity) {
                        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                    }
                } ?: run {
                    startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                }
            }
            btnSearchNow.setZoomClickEffect()
            btnSearchNow.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@MainActivity) {
                        startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                    }
                } ?: run {
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                }
            }
            btnNearbyFlight.setZoomClickEffect()
            btnNearbyFlight.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@MainActivity) {
                        startActivity(Intent(this@MainActivity, NearByActivity::class.java))
                    }
                } ?: run {
                    startActivity(Intent(this@MainActivity, NearByActivity::class.java))
                }
            }
            btnFollowedFlight.setZoomClickEffect()
            btnFollowedFlight.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@MainActivity) {
                        startActivity(Intent(this@MainActivity, TrackedActivity::class.java))
                    }
                } ?: run {
                    startActivity(Intent(this@MainActivity, TrackedActivity::class.java))
                }
            }
            btnScheduledFlight.setZoomClickEffect()
            btnScheduledFlight.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@MainActivity) {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                FlightScheduleSearchAirportActivity::class.java
                            )
                        )
                    }
                } ?: run {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            FlightScheduleSearchAirportActivity::class.java
                        )
                    )
                }
            }
            btnSavedFlight.setZoomClickEffect()
            btnSavedFlight.setOnClickListener {
                clickCount += 1
                InterstitialAdManager.mInterstitialAd?.let {
                    InterstitialAdManager.showAd(this@MainActivity) {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                FavouriteFlightActivity::class.java
                            )
                        )
                    }
                } ?: run {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            FavouriteFlightActivity::class.java
                        )
                    )
                }
            }
        }
    }

    private val viewModel: FlightAppViewModel by inject()

    private fun observeLiveData() {
        binding.pg.visible()
        viewModel.apply {
            allApiCallCompleted.observe(this@MainActivity) {
                if (it) {
                    binding.pg.invisible()
                } else {
                    binding.pg.visible()
                }
            }
            airPlanesData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        dataCollector.planes = result.data
                        job = CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val matchingAirplanes = dataCollector.planes.filter { airplane ->
                                    dataCollector.flights.any { flight ->
                                        flight.aircraft?.regNumber?.equals(
                                            airplane.numberRegistration, ignoreCase = true
                                        ) == true
                                    }
                                }
                                dataCollector.matchingAirplanes = matchingAirplanes
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Error in matchingAirplanes: ${e.message}")
                            }
                        }
                    }

                    is Resource.Error -> {
                    }
                }
            }
            airPortsData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        dataCollector.airports = result.data
                    }

                    is Resource.Error -> {}
                }
            }

            staticAirLineData.observe(this@MainActivity) { response ->
                when (response) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        dataCollector.staticAirlines = response.data
                    }

                    is Resource.Error -> {}
                }
            }

            liveFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        getAirPorts()
                        getStaticAirLines()
                        getScheduleFlight()
                        dataCollector.flights = result.data
                    }

                    is Resource.Error -> {}
                }
            }

            scheduleFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        dataCollector.schedules = result.data
                    }

                    is Resource.Error -> {}
                }
            }

            citiesData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        dataCollector.cities = result.data
                    }

                    is Resource.Error -> {}
                }
            }
        }
    }

    private var job: Job? = null
    private val dataCollector: DataCollector by inject()

    override fun onDestroy() {
        super.onDestroy()
    }
}