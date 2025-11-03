package com.example.flighttrackerappnew.presentation.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.RenderMode
import com.example.flighttrackerappnew.R
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
import com.example.flighttrackerappnew.presentation.utils.lon
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel: FlightAppViewModel by inject()

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
                                this@MainActivity, LiveMapFlightTrackerLockedActivity::class.java
                            )
                        )
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
                        {

                        })
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
                        isCitiesApiSuccess = false
                    }

                    is Resource.Success -> {
                        isCitiesApiSuccess = true
                    }

                    is Resource.Error -> {
                        isCitiesApiSuccess = false
                    }
                }
            }

            airCraftData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isAirCraftApiSuccess = false
                    }

                    is Resource.Success -> {
                        isAirCraftApiSuccess = true
                    }

                    is Resource.Error -> {
                        isAirCraftApiSuccess = false
                    }
                }
            }
            airPortsData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isAirPortApiSuccess = false
                    }

                    is Resource.Success -> {
                        isAirPortApiSuccess = true
                    }

                    is Resource.Error -> {
                        isAirPortApiSuccess = false
                    }
                }
            }

            staticAirLineData.observe(this@MainActivity) { response ->
                when (response) {
                    is Resource.Loading -> {
                        isAirLineApiSuccess = false
                    }

                    is Resource.Success -> {
                        isAirLineApiSuccess = true
                    }

                    is Resource.Error -> {
                        isAirLineApiSuccess = false
                    }
                }
            }
        }
    }

    private fun observeDynamicData() {
        viewModel.apply {
            liveFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isFlightTrackerApiSuccess = false
                        showLoading()
                    }

                    is Resource.Success -> {
                        isFlightTrackerApiSuccess = true
                        hideLoading()
                    }

                    is Resource.Error -> {
                        isFlightTrackerApiSuccess = false
                        hideLoading()
                        showDialog()
                    }
                }
            }

            scheduleFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isFlightScheduleApiSuccess = false
                    }

                    is Resource.Success -> {
                        isFlightScheduleApiSuccess = true
                    }

                    is Resource.Error -> {
                        isFlightScheduleApiSuccess = false
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

    private fun showDialog() {
        CustomDialogBuilder(this).setLayout(R.layout.dialog_retry).setCancelable(false)
            .setPositiveClickListener {
                showLoading()
                it.dismiss()
                getLongLatFirst()
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    private var job: Job? = null

    override fun onDestroy() {
        super.onDestroy()
    }
}