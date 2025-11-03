package com.example.flighttrackerappnew.presentation.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.futureSchedule.CustomFutureSchedule
import com.example.flighttrackerappnew.data.model.futureSchedule.FutureScheduleItem
import com.example.flighttrackerappnew.databinding.ActivityFlightScheduleBinding
import com.example.flighttrackerappnew.presentation.adapter.FutureScheduleFlightAdapter
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_FLIGHT_SCHEDULED
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isCitiesApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFutureScheduleApiSuccess
import com.example.flighttrackerappnew.presentation.utils.logDebug
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
    private var futureScheduledFlightData: List<FutureScheduleItem> = emptyList()
    private var citiesList = listOf<CitiesDataItems>()
    private val adapter = FutureScheduleFlightAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeLiveData()
        viewListener()
    }

    private fun loadAd() {
        NATIVE_FLIGHT_SCHEDULED.apply {
            loadNativeAd(
                this@FlightScheduleActivity,
                RemoteConfigManager.getBoolean("NATIVE_FLIGHT_SCHEDULED")
            )
        }
    }

    private fun viewListener() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun observeLiveData() {
        viewModel.apply {
            citiesData.observe(this@FlightScheduleActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isCitiesApiSuccess = false
                    }

                    is Resource.Success -> {
                        citiesList = result.data
                        if (isFutureScheduleApiSuccess) {
                            setData()
                        }
                        isCitiesApiSuccess = true
                    }

                    is Resource.Error -> false
                }
            }
            futureScheduleFlightData.observe(this@FlightScheduleActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isFutureScheduleApiSuccess = false
                        binding.pg.visible()
                        binding.conPlaceholder.invisible()
                    }

                    is Resource.Success -> {
                        futureScheduledFlightData = result.data
                        if (isCitiesApiSuccess) {
                            setData()
                        }
                        isFutureScheduleApiSuccess = true
                    }

                    is Resource.Error -> {
                        logDebug("aksjdb", result.message.toString())
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

    fun setData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val customData = getData()
            withContext(Dispatchers.Main) {
                if (customData.isNotEmpty()) {
                    loadAd()
                    binding.conPlaceholder.invisible()
                    binding.recyclerView.visible()
                    binding.recyclerView.adapter = adapter
                    binding.pg.invisible()
                    adapter.setList(customData)
                } else {
                    binding.apply {
                        recyclerView.invisible()
                        pg.invisible()
                        conPlaceholder.visible()
                    }
                }
            }
        }
    }

    private fun getData(): ArrayList<CustomFutureSchedule> {
        val customFutureScheduleList = ArrayList<CustomFutureSchedule>()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

        futureScheduledFlightData.forEachIndexed { index, item ->
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