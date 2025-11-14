package com.example.flighttrackerappnew.presentation.activities.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.RenderMode
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.remoteStatus.DataStatus
import com.example.flighttrackerappnew.data.remoteStatus.RemoteDataProvider.airPlaneStatus
import com.example.flighttrackerappnew.data.remoteStatus.RemoteDataProvider.airPortStatus
import com.example.flighttrackerappnew.data.remoteStatus.RemoteDataProvider.airlineStatus
import com.example.flighttrackerappnew.data.remoteStatus.RemoteDataProvider.cityStatus
import com.example.flighttrackerappnew.data.remoteStatus.RemoteDataProvider.flight
import com.example.flighttrackerappnew.data.remoteStatus.RemoteDataProvider.flightSchedule
import com.example.flighttrackerappnew.databinding.ActivityMainBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.NearByActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.clickCount
import com.example.flighttrackerappnew.presentation.utils.getCurrentCountryLatLon
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isAirCraftApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isAirLineApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isAirPortApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isCitiesApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFlightScheduleApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFlightTrackerApiSuccess
import com.example.flighttrackerappnew.presentation.utils.lat
import com.example.flighttrackerappnew.presentation.utils.loadAppOpen
import com.example.flighttrackerappnew.presentation.utils.logDebug
import com.example.flighttrackerappnew.presentation.utils.lon
import com.example.flighttrackerappnew.presentation.utils.matchingAirplanes
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
    private var planes: List<AirPlaneItems> = emptyList()
    var flights: List<FlightDataItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewListener()
        observeLiveData()
        onBackPress()

        binding.ivAirplaneHome.renderMode = RenderMode.HARDWARE
    }

    private fun loadAd() {
        NativeAdProvider.NATIVE_HOME.apply {
            loadNativeAd(
                this@MainActivity, RemoteConfigManager.getBoolean("NATIVE_HOME")
            )
            showNativeAd(
                adGroup = NativeAdProvider.NATIVE_HOME,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_home_screen,
                activity = this@MainActivity
            )
        }

        BannerAdProvider.BANNER_HOME.apply {
            loadAndShowBannerAd(
                context = this@MainActivity,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {})
        }
    }

    override fun onResume() {
        super.onResume()
        if (config.isPremiumUser) {
            binding.apply {
                PremiumScreenIcon.invisible()
                flAdplaceholder.gone()
                adContainerView.gone()
            }
        } else {
            loadAd()
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
        CustomDialogBuilder(this).setLayout(R.layout.dialog_exit_app).setCancelable(false)
            .setPositiveClickListener {
                loadAppOpen = false
                it.dismiss()
                job?.cancel()
                finish()
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    private fun viewListener() {
        binding.apply {
            btnLiveFlightTracker.setOnClickListener {
                if (binding.pg.isVisible) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                if (isFlightTrackerApiSuccess && isFlightScheduleApiSuccess) {
                    if (config.isPremiumUser) {
                        startActivity(
                            Intent(
                                this@MainActivity, LiveMapFlightTrackerActivity::class.java
                            )
                        )
                    } else {
                        startActivity(
                            Intent(
                                this@MainActivity, LiveMapFlightTrackerActivity::class.java
                            )
                        )
//                        startActivity(
//                            Intent(
//                                this@MainActivity, LiveMapFlightTrackerLockedActivity::class.java
//                            )
//                        )
                    }
                } else {
                    showDialog()
                }
            }

            btnSetting.setOnClickListener {
                if (binding.pg.isVisible) {
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
                    },
                    {

                    })
            }

            btnSearchNow.setOnClickListener {
                if (binding.pg.isVisible) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                if (isFlightTrackerApiSuccess && isFlightScheduleApiSuccess) {
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
                        },
                        {}
                    )
                } else {
                    showDialog()
                }
            }

            btnNearbyFlight.setOnClickListener {
                if (binding.pg.isVisible) {
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

                    },
                    {

                    })
            }

            btnScheduledFlight.setOnClickListener {
                if (binding.pg.isVisible) {
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
                                this@MainActivity, FlightScheduleSearchAirportActivity::class.java
                            )
                        )
                    },
                    {})
            }

            PremiumScreenIcon.setOnClickListener {
                if (binding.pg.isVisible) {
                    this@MainActivity.showToast("Wait!!")
                    return@setOnClickListener
                }
                showPremiumScreen()
            }

            btnFollowedFlight.setOnClickListener {
                if (binding.pg.isVisible) {
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
                                this@MainActivity, FollowedFlightActivity::class.java
                            )
                        )
                    },
                    {

                    })
            }

            btnStarredFlight.setOnClickListener {
                if (binding.pg.isVisible) {
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
                                this@MainActivity, StarredFlightActivity::class.java
                            )
                        )
                    },
                    {

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
        observeDynamicData()
        observerStaticData()
        observerOtherData()
    }

    private fun observerOtherData() {
        viewModel.apply {
            followFlightData.observe(this@MainActivity) {

            }
            favFlightData.observe(this@MainActivity) {

            }
        }
    }

    private fun observerStaticData() {
        viewModel.apply {
            citiesData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        cityStatus._statusFlow.value = DataStatus.Loading
                        logDebug("Flight Trackers", "citiesData ==> Loading")
                        isCitiesApiSuccess = false
                    }

                    is Resource.Success -> {
                        cityStatus._statusFlow.value = DataStatus.SUCCESS
                        logDebug("Flight Trackers", "citiesData ==> Success")
                        isCitiesApiSuccess = true
                    }

                    is Resource.Error -> {
                        cityStatus._statusFlow.value = DataStatus.Failure
                        logDebug("Flight Trackers", "citiesData ==> Error")
                        isCitiesApiSuccess = false
                    }
                }
            }

            airCraftData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        airPlaneStatus._statusFlow.value = DataStatus.Loading
                        isAirCraftApiSuccess = false
                        logDebug("Flight Trackers", "airCraftData ==> Loading")
                    }

                    is Resource.Success -> {
                        airPlaneStatus._statusFlow.value = DataStatus.SUCCESS
                        logDebug("Flight Trackers", "airCraftData ==> Success")
                        planes = result.data
                        isAirCraftApiSuccess = true
                        if (isFlightTrackerApiSuccess) {
                            getMatchingAirPlanes(planes, flights)
                        }
                    }

                    is Resource.Error -> {
                        airPlaneStatus._statusFlow.value = DataStatus.Failure
                        logDebug("Flight Trackers", "airCraftData ==> Error")
                        isAirCraftApiSuccess = false
                    }
                }
            }
            airPortsData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        airPortStatus._statusFlow.value = DataStatus.Loading
                        logDebug("Flight Trackers", "airPortsData Loading")
                        isAirPortApiSuccess = false
                    }

                    is Resource.Success -> {
                        airPortStatus._statusFlow.value = DataStatus.SUCCESS
                        logDebug("Flight Trackers", "airPortsData Success")
                        isAirPortApiSuccess = true
                    }

                    is Resource.Error -> {
                        airPortStatus._statusFlow.value = DataStatus.Failure
                        isAirPortApiSuccess = false
                        logDebug("Flight Trackers", "airPortsData Error")
                    }
                }
            }

            staticAirLineData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        airlineStatus._statusFlow.value = DataStatus.Loading
                        isAirLineApiSuccess = false
                        logDebug("Flight Trackers", "staticAirLineData Loading")
                    }

                    is Resource.Success -> {
                        airlineStatus._statusFlow.value = DataStatus.SUCCESS
                        isAirLineApiSuccess = true
                        logDebug("Flight Trackers", "staticAirLineData Success")
                    }

                    is Resource.Error -> {
                        airlineStatus._statusFlow.value = DataStatus.Failure
                        isAirLineApiSuccess = false
                        logDebug("Flight Trackers", "staticAirLineData Error")
                    }
                }
            }
        }
    }


    fun getMatchingAirPlanes(planes: List<AirPlaneItems>, flights: List<FlightDataItem>) {
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val matchingAirplane = planes.filter { airplane ->
                    flights.any { flight ->
                        flight.aircraft?.regNumber?.equals(
                            airplane.numberRegistration, ignoreCase = true
                        ) == true
                    }
                }
                matchingAirplanes = matchingAirplane
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in matchingAirplanes: ${e.message}")
            }
        }
    }

    private fun observeDynamicData() {
        viewModel.apply {
            liveFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        flight._statusFlow.value = DataStatus.Loading
                        logDebug("Flight Trackers", "liveFlightData Loading")
                        isFlightTrackerApiSuccess = false
                        showLoading()
                    }

                    is Resource.Success -> {
                        flight._statusFlow.value = DataStatus.SUCCESS
                        logDebug("Flight Trackers", "liveFlightData Success")
                        flights = result.data
                        flights.forEach {
                            logDebug("Flight Trackers", "$it")
                        }
                        isFlightTrackerApiSuccess = true
                        if (flightSchedule.status == DataStatus.SUCCESS || flightSchedule.status == DataStatus.Failure) {
                            hideLoading()
                        }
                        if (isAirCraftApiSuccess) {
                            getMatchingAirPlanes(planes, flights)
                        }
                    }

                    is Resource.Error -> {
                        flight._statusFlow.value = DataStatus.Failure
                        logDebug("Flight Trackers", "liveFlightData Error")
                        isFlightTrackerApiSuccess = false
                        hideLoading()
                        showDialog()
                        logDebug("My__tAG", result.message)
                        this@MainActivity.showToast(result.message)
                    }
                }
            }

            scheduleFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        flightSchedule._statusFlow.value = DataStatus.Loading
                        logDebug("Flight Trackers", "scheduleFlightData Loading")
                        isFlightScheduleApiSuccess = false
                    }

                    is Resource.Success -> {
                        flightSchedule._statusFlow.value = DataStatus.SUCCESS
                        logDebug("Flight Trackers", "scheduleFlightData Success")
                        if (flight.status == DataStatus.SUCCESS || flight.status == DataStatus.Failure) {
                            hideLoading()
                        }
                        isFlightScheduleApiSuccess = true
                    }

                    is Resource.Error -> {
                        flightSchedule._statusFlow.value = DataStatus.Failure
                        logDebug("Flight Trackers", "scheduleFlightData Error")
                        isFlightScheduleApiSuccess = false
                        hideLoading()
                        showDialog()
                        logDebug("My__tAG", result.message)
                        this@MainActivity.showToast(result.message)
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
        lat?.let { lat ->
            lon?.let { lon ->
                if (isFlightTrackerApiSuccess) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getScheduleFlight()
                    }
                } else if (isFlightScheduleApiSuccess) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getLiveFlight(
                            lat, lon, RemoteConfigManager.getString("distance").toInt()
                        )
                    }
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getDynamicApiData(
                            lat, lon, RemoteConfigManager.getString("distance").toInt()
                        )
                    }
                }
            }
        }
    }

    private var dialog: Dialog? = null

    private fun showDialog() {
        if (dialog == null) {
            dialog = CustomDialogBuilder(this).setLayout(R.layout.dialog_retry).setCancelable(false)
                .setPositiveClickListener {
                    showLoading()
                    it.dismiss()
                    getLongLatFirst()
                    dialog = null
                }.setNegativeClickListener {
                    it.dismiss()
                    dialog = null
                }.show()
        }
    }

    private var job: Job? = null

    override fun onDestroy() {
        super.onDestroy()
    }
}