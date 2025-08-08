package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.databinding.ActivityFlightScheduleSearchAirportBinding
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adapter.SearchAirportAdapter
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.airportCode
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject

class FlightScheduleSearchAirportActivity :
    BaseActivity<ActivityFlightScheduleSearchAirportBinding>(
        ActivityFlightScheduleSearchAirportBinding::inflate
    ) {

    private var adapter = SearchAirportAdapter()
    private var airportsList = listOf<AirportsDataItems>()
    private val dataCollector: DataCollector by inject()
    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        setData()
        viewListener()
        adapter.setListener {
            airportCode = it.codeIataAirport.toString()
            startActivity(Intent(this, FlightScheduleTypeAirportActivity::class.java))
        }
    }

    private fun setData() {
        airportsList = dataCollector.airports
        binding.recyclerView.adapter = adapter
        adapter.setList(airportsList)
        if (airportsList.isNotEmpty()) {
            val NATIVE_FLIGHT_SCHEDULED_SEARCH =
                RemoteConfigManager.getBoolean("NATIVE_FLIGHT_SCHEDULED_SEARCH")
            if (NATIVE_FLIGHT_SCHEDULED_SEARCH) {
                binding.flAdplaceholder.visible()
                nativeAdController.apply {
                    loadNativeAd(
                        this@FlightScheduleSearchAirportActivity,
                        app.getString(R.string.NATIVE_FLIGHT_SCHEDULED_SEARCH)
                    )
                    showNativeAd(this@FlightScheduleSearchAirportActivity, binding.flAdplaceholder)
                }
            }
        }
    }

    private fun viewListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            tvAirports.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    val adapter = binding.recyclerView.adapter as SearchAirportAdapter
                    val filterList: List<AirportsDataItems> = airportsList.filter {
                        it.nameAirport?.lowercase()?.startsWith(text.lowercase()) == true
                    }

                    adapter.setList(filterList)

                    if (filterList.isEmpty()) {
                        searchGroup.visible()
                    } else {
                        searchGroup.invisible()
                    }
                }
            })
        }
    }
}