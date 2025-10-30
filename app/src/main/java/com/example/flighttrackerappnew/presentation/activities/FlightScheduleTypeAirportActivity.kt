package com.example.flighttrackerappnew.presentation.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityFlightScheduleTypeAirportBinding
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_FLIGHT_SCHEDULED_TYPE
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.clickCount
import com.example.flighttrackerappnew.presentation.utils.flightType
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.selectedDate
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.startDate
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import org.koin.android.ext.android.inject
import java.util.Calendar

class FlightScheduleTypeAirportActivity :
    BaseActivity<ActivityFlightScheduleTypeAirportBinding>(ActivityFlightScheduleTypeAirportBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        viewListener()
        loadAd()
    }

    private fun loadAd() {
        NATIVE_FLIGHT_SCHEDULED_TYPE.apply {
            loadNativeAd(
                this@FlightScheduleTypeAirportActivity,
                RemoteConfigManager.getBoolean("NATIVE_FLIGHT_SCHEDULED_TYPE")
            )
            showNativeAd(
                adGroup = NATIVE_FLIGHT_SCHEDULED_TYPE,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_layout_view_with_media,
              activity =   this@FlightScheduleTypeAirportActivity
            )
        }
    }

    private fun viewListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            binding.conDate.setOnClickListener {
                showDatePicker()
            }
            consArrival.setOnClickListener {
                flightType = "arrival"
                binding.consArrival.background = ContextCompat.getDrawable(
                    this@FlightScheduleTypeAirportActivity,
                    R.drawable.search_airport_s_tab_bg

                )
                binding.tvFollow.setTextColor(
                    ContextCompat.getColor(
                        this@FlightScheduleTypeAirportActivity,
                        R.color.primary
                    )
                )
                binding.tvDetails.setTextColor(
                    ContextCompat.getColor(
                        this@FlightScheduleTypeAirportActivity,
                        R.color.acc1
                    )
                )
                binding.conDeparture.background = ContextCompat.getDrawable(
                    this@FlightScheduleTypeAirportActivity,
                    R.drawable.search_airport_uns_tab_bg
                )
            }
            conDeparture.setOnClickListener {
                flightType = "departure"
                binding.consArrival.background = ContextCompat.getDrawable(
                    this@FlightScheduleTypeAirportActivity,
                    R.drawable.search_airport_uns_tab_bg
                )
                binding.conDeparture.background = ContextCompat.getDrawable(
                    this@FlightScheduleTypeAirportActivity,
                    R.drawable.search_airport_s_tab_bg
                )
                binding.tvFollow.setTextColor(
                    ContextCompat.getColor(
                        this@FlightScheduleTypeAirportActivity,
                        R.color.acc1
                    )
                )
                binding.tvDetails.setTextColor(
                    ContextCompat.getColor(
                        this@FlightScheduleTypeAirportActivity,
                        R.color.primary
                    )
                )
            }
            btnSearch.setOnClickListener {
                if (startDate.isEmpty()) {
                    this@FlightScheduleTypeAirportActivity.showToast("Please select date")
                } else {
                    clickCount += 1
                    InterstitialAdManager.loadInterstitialAd(
                        ignoreClickCount = true,
                        showLoadingScreenWithDelay = 0L,
                        showLoadingAsLoadAdRequestCalled = true,
                        interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                        showWhenReady = true,
                        activity = this@FlightScheduleTypeAirportActivity,
                        adUnitId = app.getString(R.string.INTERSTITIAL_MAP_STYLE),
                        isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_MAP_STYLE"),
                        adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                        {
                            viewModel.getFutureScheduleFlight()
                            startActivity(
                                Intent(
                                    this@FlightScheduleTypeAirportActivity,
                                    FlightScheduleActivity::class.java
                                )
                            )
                        }, {

                        })
                }
            }
        }
    }

    private val viewModel: FlightAppViewModel by inject()

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formatted =
                    "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                binding.tvDate.text = formatted
                selectedDate = formatted
                startDate = formatted
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        startDate = ""
    }
}