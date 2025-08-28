package com.example.flighttrackerappnew.presentation.activities

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.futureSchedule.CustomFutureSchedule
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.databinding.ActivityFlightScheduleBinding
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adapter.FutureScheduleFlightAdapter
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class FlightScheduleActivity :
    BaseActivity<ActivityFlightScheduleBinding>(ActivityFlightScheduleBinding::inflate) {

    private val viewModel: FlightAppViewModel by inject()
    private val dataCollector: DataCollector by inject()
    private val adapter = FutureScheduleFlightAdapter()

    private lateinit var liveFlight: List<FlightDataItem>
    private lateinit var airportsDataList: List<AirportsDataItems>
    private var citiesList = listOf<CitiesDataItems>()
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var airPlane = listOf<AirPlaneItems>()
    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        citiesList = dataCollector.cities
        airportsDataList = dataCollector.airports
        scheduleFlightList = dataCollector.schedules
        airPlane = dataCollector.planes
        liveFlight = dataCollector.flights

        binding.recyclerView.adapter = adapter
        observeLiveData()
        viewListener()

        loadAd()
    }

    private fun loadAd() {
        val NATIVE_FLIGHT_SCHEDULED =
            RemoteConfigManager.getBoolean("NATIVE_FLIGHT_SCHEDULED")
        if (NATIVE_FLIGHT_SCHEDULED) {
            nativeAdController.apply {
                loadNativeAd(
                    this@FlightScheduleActivity,
                    app.getString(R.string.NATIVE_FLIGHT_SCHEDULED),
                )
            }
        }

    }

    private fun viewListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeLiveData() {
        viewModel.apply {
            futureScheduleFlightData.observe(this@FlightScheduleActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.pg.visible()
                        binding.conPlaceholder.invisible()
                    }

                    is Resource.Success -> {
                        dataCollector.futureScheduleFlightData = result.data

                        lifecycleScope.launch(Dispatchers.IO) {
                            val customData = setData()
                            withContext(Dispatchers.Main) {
                                if (customData.isNotEmpty()) {
                                    binding.conPlaceholder.invisible()
                                    binding.recyclerView.visible()
                                    binding.pg.invisible()
                                    adapter.setList(customData, nativeAdController)

                                } else {
                                    binding.recyclerView.invisible()
                                    binding.conPlaceholder.visible()
                                    binding.pg.invisible()
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        binding.apply {
                            recyclerView.invisible()
                            pg.invisible()
                            conPlaceholder.visible()
                        }
                    }
                }
            }
        }
    }

    private fun setData(): ArrayList<CustomFutureSchedule> {
        val customFutureScheduleList = ArrayList<CustomFutureSchedule>()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

        dataCollector.futureScheduleFlightData.forEachIndexed { index, item ->
            val iataArrival = item.arrival?.iataCode?.uppercase()
            val iataDeparture = item.departure?.iataCode?.uppercase()

            val arrivalCity = citiesList.firstOrNull {
                it.codeIataCity.equals(iataArrival, ignoreCase = true)
            }?.nameCity

            val departureCity = citiesList.firstOrNull {
                it.codeIataCity.equals(iataDeparture, ignoreCase = true)
            }?.nameCity

            val depTimeRaw = item.departure?.scheduledTime
            val arrTimeRaw = item.arrival?.scheduledTime

            val flightDuration = if (!depTimeRaw.isNullOrBlank() && !arrTimeRaw.isNullOrBlank()) {
                try {
                    val depDate = formatter.parse(depTimeRaw)
                    val arrDate = formatter.parse(arrTimeRaw)

                    if (depDate != null && arrDate != null) {
                        val diffInMillis = arrDate.time - depDate.time
                        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60
                        "${hours}h ${minutes}m"
                    } else {
                        null
                    }
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }

            val NATIVE_FLIGHT_SCHEDULED =
                RemoteConfigManager.getBoolean("NATIVE_FLIGHT_SCHEDULED")
            if (NATIVE_FLIGHT_SCHEDULED && !config.isPremiumUser) {
                if (index == 1) {
                    customFutureScheduleList.add(
                        CustomFutureSchedule(
                            arrivalCity = arrivalCity.orEmpty(),
                            departureCity = departureCity.orEmpty(),
                            arrivalCityIataCode = item.arrival?.iataCode.orEmpty(),
                            departureCityIataCode = item.departure?.iataCode.orEmpty(),
                            airLineIataCode = item.airline?.iataCode.orEmpty(),
                            flightNo = item.flight?.iataNumber.orEmpty(),
                            departureTime = depTimeRaw.orEmpty(),
                            arrivalTime = arrTimeRaw.orEmpty(),
                            flightTime = flightDuration,
                            type = 2
                        )
                    )
                }

                if (index % 2 == 1 && index > 2) {
                    customFutureScheduleList.add(
                        CustomFutureSchedule(
                            arrivalCity = arrivalCity.orEmpty(),
                            departureCity = departureCity.orEmpty(),
                            arrivalCityIataCode = item.arrival?.iataCode.orEmpty(),
                            departureCityIataCode = item.departure?.iataCode.orEmpty(),
                            airLineIataCode = item.airline?.iataCode.orEmpty(),
                            flightNo = item.flight?.iataNumber.orEmpty(),
                            departureTime = depTimeRaw.orEmpty(),
                            arrivalTime = arrTimeRaw.orEmpty(),
                            flightTime = flightDuration,
                            type = 2
                        )
                    )
                }
            }

            customFutureScheduleList.add(
                CustomFutureSchedule(
                    arrivalCity = arrivalCity.orEmpty(),
                    departureCity = departureCity.orEmpty(),
                    arrivalCityIataCode = item.arrival?.iataCode.orEmpty(),
                    departureCityIataCode = item.departure?.iataCode.orEmpty(),
                    airLineIataCode = item.airline?.iataCode.orEmpty(),
                    flightNo = item.flight?.iataNumber.orEmpty(),
                    departureTime = depTimeRaw.orEmpty(),
                    arrivalTime = arrTimeRaw.orEmpty(),
                    flightTime = flightDuration,
                    type = 1
                )
            )
        }
        return customFutureScheduleList
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearFutureFlightData()
    }
}