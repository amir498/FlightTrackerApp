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
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_HOME
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_HOME
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
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel: FlightAppViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.subMain.setPadding(
            0,
            getStatusBarHeight,
            0,
            0
        )

        viewListener()
        observeLiveData()
        onBackPress()

        binding.ivAirplaneHome.renderMode = RenderMode.HARDWARE
    }

    private fun loadAd() {
        NATIVE_HOME.apply {
            loadNativeAd(
                this@MainActivity,
                RemoteConfigManager.getBoolean("NATIVE_HOME")
            )
            showNativeAd(
                adGroup = NATIVE_HOME,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_home_screen,
                activity = this@MainActivity
            )
        }

        BANNER_HOME.apply {
            loadAndShowBannerAd(
                context = this@MainActivity,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {}
            )
        }
    }

    override fun onResume() {
        super.onResume()
        loadAd()
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

            btnSetting.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                clickCount += 1
                InterstitialAdManager.loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@MainActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_HOME),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_HOME"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                    }, {

                    })
            }

            btnSearchNow.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                clickCount += 1
                InterstitialAdManager.loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@MainActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_HOME),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_HOME"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                    }, {

                    })
            }

            btnNearbyFlight.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                clickCount += 1
                InterstitialAdManager.loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@MainActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_HOME),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_HOME"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(Intent(this@MainActivity, NearByActivity::class.java))

                    }, {

                    })
            }

            btnFollowedFlight.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                clickCount += 1
                InterstitialAdManager.loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@MainActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_HOME),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_HOME"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(Intent(this@MainActivity, FollowedFlightActivity::class.java))
                    }, {

                    })
            }

            btnScheduledFlight.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                clickCount += 1
                InterstitialAdManager.loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@MainActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_HOME),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_HOME"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                FlightScheduleSearchAirportActivity::class.java
                            )
                        )
                    }, {}
                )
            }

            PremiumScreenIcon.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                showPremiumScreen()
            }

            btnSavedFlight.setOnClickListener {
                if (checkTrue()) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                clickCount += 1
                InterstitialAdManager.loadInterstitialAd(
                    ignoreClickCount = false,
                    showLoadingScreenWithDelay = 0L,
                    showLoadingAsLoadAdRequestCalled = true,
                    interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                    showWhenReady = true,
                    activity = this@MainActivity,
                    adUnitId = app.getString(R.string.INTERSTITIAL_HOME),
                    isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_HOME"),
                    adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                    {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                FavouriteFlightActivity::class.java
                            )
                        )
                    }, {

                    })
            }
        }
    }

    private fun showPremiumScreen() {
        config.startDiscountIfNeeded()

        val isDiscountActive = config.isDiscountActive()

        if (isDiscountActive) {
            val intent = Intent(this@MainActivity, PremiumActivity2::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this@MainActivity, PremiumActivity::class.java)
            startActivity(intent)
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
                        Log.d("MY----TAG", "observeLiveData: LiveFlightData Loading")
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        dataCollector.flights = result.data
                        Log.d("MY----TAG", "observeLiveData: Success — ${result.data.size} flights loaded")
                    }

                    is Resource.Error -> {
                        hideLoading()
                        showToast("Error: ${result.message}")
                        Log.e("MY----TAG", "observeLiveData: Error — ${result.message}", result.throwable)
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

    private fun showLoading() {
        binding.pg.visible()
        binding.pgText.visible()
        binding.ivTransparent.visible()
    }

    private fun hideLoading() {
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