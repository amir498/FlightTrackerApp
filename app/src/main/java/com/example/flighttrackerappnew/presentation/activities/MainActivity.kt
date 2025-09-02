package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import com.airbnb.lottie.RenderMode
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityMainBinding
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.adManager.AppOpenAdManager
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adManager.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.clickCount
import com.example.flighttrackerappnew.presentation.utils.getCurrentCountryLatLon
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isFromDetail
import com.example.flighttrackerappnew.presentation.utils.lastSelectedPlane
import com.example.flighttrackerappnew.presentation.utils.lat
import com.example.flighttrackerappnew.presentation.utils.loadAppOpen
import com.example.flighttrackerappnew.presentation.utils.lon
import com.example.flighttrackerappnew.presentation.utils.setZoomClickEffect
import com.example.flighttrackerappnew.presentation.utils.showToast
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
    private val viewModel: FlightAppViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bannerHome =
            RemoteConfigManager.getBoolean("BANNER_HOME")

        val nativeHome =
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

        if (nativeHome && !config.isPremiumUser) {
            binding.flAdplaceholder.visible()
            bannerAdManager.loadAd(true, this, app.getString(R.string.BANNER_HOME), {
                bannerAdManager.showBannerAd(
                    binding.adContainerView,
                    this@MainActivity,
                    null
                )
            }, {})
        }
        if (bannerHome && !config.isPremiumUser) {
            binding.adContainerView.visible()
            loadAd()
        }

        binding.ivAirplaneHome.renderMode = RenderMode.HARDWARE
    }

    private fun loadInterstitialAd() {
        if (!config.isPremiumUser) {
            val interstitialHome =
                RemoteConfigManager.getBoolean("INTERSTITIAL_HOME")
            if (interstitialHome) {
                InterstitialAdManager.loadInterstitialAd(
                    this,
                    app.getString(R.string.INTERSTITIAL_HOME),
                    {},
                    {},
                    {})
            }
        } else {
            InterstitialAdManager.mInterstitialAd = null
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
        if (config.isPremiumUser) {
            binding.PremiumScreenIcon.invisible()
            binding.flAdplaceholder.gone()
            binding.adContainerView.gone()
        } else {
            binding.PremiumScreenIcon.visible()
        }
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
                AppOpenAdManager.appOpenAd = null
                loadAppOpen = false
                it.dismiss()
                job?.cancel()
                finish()
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    private fun checkTrue(): Boolean {
        return binding.pg.isVisible
    }

    private fun viewListener() {
        binding.apply {
            viewMapBtn.setZoomClickEffect()
            viewMapBtn.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                if (config.isPremiumUser) {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            LiveMapFlightTrackerActivity::class.java
                        )
                    )
                } else {
                    startActivity(
                        Intent(
                            this@MainActivity,
                            LiveMapFlightTrackerLockedActivity::class.java
                        )
                    )
                }
            }

            btnSetting.setZoomClickEffect()
            btnSetting.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
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
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
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
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
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
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
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
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
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

            PremiumScreenIcon.setZoomClickEffect()
            PremiumScreenIcon.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                startActivity(Intent(this@MainActivity, PremiumActivity::class.java))
            }

            btnSavedFlight.setZoomClickEffect()
            btnSavedFlight.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
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

    private fun observeLiveData() {
        viewModel.getFollowFlightData()
        viewModel.apply {
            airPlanesData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MY----TAG", "observeLiveData:airPlanesData Loading")
                    }

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
                        Log.d("MY----TAG", "observeLiveData:No Airplane Data found ")
                        showToast("No Airplane Data found")

                    }
                }
            }
            airPortsData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MY----TAG", "observeLiveData:airPortsData Loading")
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        dataCollector.airports = result.data
                    }

                    is Resource.Error -> {
                        hideLoading()
                        showToast("No Airport Data found")
                        Log.d("MY----TAG", "observeLiveData:No Airport Data found")
                    }
                }
            }

            staticAirLineData.observe(this@MainActivity) { response ->
                when (response) {
                    is Resource.Loading -> {
                        Log.d("MY----TAG", "observeLiveData:staticAirLineData Loading")
                    }

                    is Resource.Success -> {
                        dataCollector.staticAirlines = response.data
                    }

                    is Resource.Error -> {
                        showToast("No Airlines Data found")
                        Log.d("MY----TAG", "observeLiveData:No Airlines Data found")
                    }
                }
            }

            liveFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MY----TAG", "observeLiveData:liveFlightData Loading")
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        dataCollector.flights = result.data
                    }

                    is Resource.Error -> {
                        hideLoading()
                        showToast("No LiveFlight Data found")
                        Log.d("MY----TAG", "observeLiveData:No LiveFlight Data found")
                        showDialog()
                    }
                }
            }

            scheduleFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MY----TAG", "observeLiveData:scheduleFlightData Loading")
                    }

                    is Resource.Success -> {
                        dataCollector.schedules = result.data
                    }

                    is Resource.Error -> {
                        showToast("No scheduleFlightData found")
                        Log.d("MY----TAG", "observeLiveData:No scheduleFlightData found")
                    }
                }
            }

            citiesData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        Log.d("MY----TAG", "observeLiveData:citiesData Loading")

                    }

                    is Resource.Success -> {
                        dataCollector.cities = result.data
                    }

                    is Resource.Error -> {
                        showToast("No citiesData found")
                        Log.d("MY----TAG", "observeLiveData:No citiesData found")
                    }
                }
            }
        }
    }

    private fun showLoading(){
        binding.pg.visible()
        binding.pgText.visible()
        binding.ivTransparent.visible()
    }

    private fun hideLoading(){
        binding.pg.invisible()
        binding.pgText.invisible()
        binding.ivTransparent.invisible()
    }

    private fun getLongLatFirst() {
        val pair = getCurrentCountryLatLon(this)
        lat = pair?.first
        lon = pair?.second
        lat?.let { lon?.let { it1 -> getAllApiCall(it, it1) } }
    }

    fun getAllApiCall(lat: Double, lon: Double) {
        val distance =
            RemoteConfigManager.getString("distance")
        viewModel.getAllData(lat, lon, distance.toInt()) {
        }
    }

    private fun showDialog() {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_retry)
            .setCancelable(false)
            .setPositiveClickListener {
                showLoading()
                it.dismiss()
                getLongLatFirst()
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    private var job: Job? = null
    private val dataCollector: DataCollector by inject()

    override fun onDestroy() {
        super.onDestroy()
    }
}