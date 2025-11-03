package com.example.flighttrackerappnew.presentation.activities.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.databinding.ActivityFlightScheduleSearchAirportBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.FlightScheduleTypeAirportActivity
import com.example.flighttrackerappnew.presentation.adapter.SearchAirportAdapter
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_FLIGHT_SCHEDULED_SEARCH
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.airportCode
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FlightScheduleSearchAirportActivity :
    BaseActivity<ActivityFlightScheduleSearchAirportBinding>(
        ActivityFlightScheduleSearchAirportBinding::inflate
    ) {
    private val viewModel: FlightAppViewModel by inject()
    private var adapter = SearchAirportAdapter()
    private var airportsList = listOf<AirportsDataItems>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observerData()
        viewListener()
    }

    private fun observerData() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAirPorts()
        }
        viewModel.airPortsData.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.pg.visible()
                }

                is Resource.Success -> {
                    airportsList = result.data
                    binding.pg.invisible()
                    setData()
                }

                is Resource.Error -> {
                    binding.pg.invisible()
                    showToast("No Airport Data found")
                }
            }
        }
    }

    private fun setData() {
        binding.recyclerView.adapter = adapter
        adapter.setList(airportsList)
        if (airportsList.isNotEmpty()) {
            loadAd()
        }
        adapter.setListener {
            airportCode = it.codeIataAirport.toString()
            startActivity(Intent(this, FlightScheduleTypeAirportActivity::class.java))
        }
    }

    private fun loadAd() {
        NATIVE_FLIGHT_SCHEDULED_SEARCH.apply {
            loadNativeAd(
                this@FlightScheduleSearchAirportActivity,
                RemoteConfigManager.getBoolean("NATIVE_FLIGHT_SCHEDULED_SEARCH")
            )
            showNativeAd(
                adGroup = NATIVE_FLIGHT_SCHEDULED_SEARCH,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_layout_view_with_media,
                activity = this@FlightScheduleSearchAirportActivity
            )
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