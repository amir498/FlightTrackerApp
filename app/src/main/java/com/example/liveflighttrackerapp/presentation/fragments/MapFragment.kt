package com.example.liveflighttrackerapp.presentation.fragments

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.liveflighttrackerapp.R
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.databinding.FragmentMapBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity
import com.example.liveflighttrackerapp.presentation.dialogbuilder.GeneralDialogBuilder
import com.example.liveflighttrackerapp.presentation.googleMap.MyGoogleMap
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource
import com.example.liveflighttrackerapp.presentation.utils.invisible
import com.example.liveflighttrackerapp.presentation.utils.isNetworkAvailable
import com.example.liveflighttrackerapp.presentation.utils.showToast
import com.example.liveflighttrackerapp.presentation.utils.visible
import com.example.liveflighttrackerapp.presentation.viewmodels.FlightAppViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class MapFragment : Fragment() {

    private val binding: FragmentMapBinding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    private val viewModel: FlightAppViewModel by inject()
    private val googleMap: MyGoogleMap by inject()

    private var allAircraftDetails: List<FlightDataItem>? = null
    private var depAirport: ArrayList<AirportsDataItems>? = null
    private var arvAirport: ArrayList<AirportsDataItems>? = null
    private var airLinesList = listOf<StaticAirLineItems>()
    private var scheduleFlightList = listOf<SchedulesFlightsItems>()
    private var airportsDataList = listOf<AirportsDataItems>()
    private var citiesList = listOf<CitiesDataItems>()
    private var airPlanesList = listOf<AirPlaneItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (requireContext().isNetworkAvailable()) {
            viewModel.loadAllData()
        } else {
            GeneralDialogBuilder(requireContext())
                .setLayout(R.layout.dialog_no_internet)
                .setCancelable(true)
                .setPositiveClickListener {
                    it.dismiss()
                }
                .setNegativeClickListener {
                    it.dismiss()
                }
                .show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getMarkerIcon()

        observeLiveData()
        initView()
        changeTextColor()
        viewListener()
    }

    private fun viewListener() {
        binding.ivFollow.setOnClickListener {
//            (activity as MainActivity).followFlight()
        }
    }

    private fun changeTextColor() {
        val trackerText = "Tracker"
        val numberText = "24"

        val spannable = SpannableString(trackerText + numberText)

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.text_color_p)),
            0,
            trackerText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.text_color_acc1)),
            trackerText.length,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tv2.text = spannable

    }

    private fun initView() {
        try {
            val mapFragment = childFragmentManager
                .findFragmentById(R.id.google_map_fragment) as SupportMapFragment
            googleMap.apply {
                setMapUi(mapFragment)
                listener { flightData ->
                    try {
                        depAirport =
                            airportsDataList.filter { it.codeIataAirport == flightData.departure?.iataCode } as ArrayList<AirportsDataItems>?
                        arvAirport =
                            airportsDataList.filter { it.codeIataAirport == flightData.arrival?.iataCode } as ArrayList<AirportsDataItems>?

                        drawFlightPathIfNotExists(
                            flightData, depAirport?.get(0),
                            arvAirport?.get(0)
                        )
                        (activity as MainActivity).setData(
                            flightData,
                            depAirport?.get(0),
                            arvAirport?.get(0),
                            airLinesList,
                            citiesList,
                            scheduleFlightList,
                            airPlanesList
                        )
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun observeLiveData() {
        viewModel.apply {
            airPlanesData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        airPlanesList = result.data
                    }

                    is Resource.Error -> {
                    }
                }
            }
            airPortsData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airportsDataList = result.data
                    }

                    is Resource.Error -> {
                    }
                }
            }

            staticAirLineData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airLinesList = response.data
                    }

                    is Resource.Error -> {
                        requireContext().showToast("Error: ${response.message}")
                    }
                }
            }
            liveFlightData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        getStaticAirLines()
                        getScheduleFlight()
                        allAircraftDetails = result.data

                        drawMarkersJob = lifecycleScope.launch {
                            binding.pg.visible()
                            delay(700)
                            setAirplanesData(coroutineContext[Job]!!)
                        }


                        googleMap.onCameraIdle { newVisibleBounds ->
                            drawMarkersJob?.cancel()
                            drawMarkersJob = lifecycleScope.launch {
                                binding.pg.visible()
                                delay(500)
                                setAirplanesData(coroutineContext[Job]!!)
                            }
                        }

                        googleMap.setOnCameraMoveStartedListener { reason ->
                            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                                drawMarkersJob?.cancel()
                            }
                        }

                    }

                    is Resource.Error -> {
                        binding.pg.invisible()
                        Log.d("error", "Error:${result.message} ")
                        requireContext().showToast("Error: ${result.message}")
                    }
                }
            }

            scheduleFlightData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.pg.visible()
                    }

                    is Resource.Success -> {
                        scheduleFlightList = result.data
                    }

                    is Resource.Error -> {
                        binding.pg.invisible()
                        requireContext().showToast("Error: ${result.message}")
                    }
                }
            }
            citiesData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.pg.visible()
                    }

                    is Resource.Success -> {
                        citiesList = result.data
                    }

                    is Resource.Error -> {
                        binding.pg.invisible()
                        requireContext().showToast("Error: ${result.message}")
                    }
                }
            }
        }
    }

    private suspend fun setAirplanesData(currentJob: Job) {
        val visibleBounds = withContext(Dispatchers.Main) {
            googleMap.getVisibleBounds()
        }

        val visibleFlights = withContext(Dispatchers.Default) {
            currentJob.ensureActive()
            allAircraftDetails
                ?.asSequence()
                ?.filter {
                    it.status == "en-route" &&
                            !it.departure?.iataCode.isNullOrEmpty() &&
                            !it.arrival?.iataCode.isNullOrEmpty()
                }
                ?.filter { flight ->
                    currentJob.ensureActive()
                    val lat = flight.geography?.latitude
                    val lng = flight.geography?.longitude
                    lat != null && lng != null && visibleBounds?.contains(LatLng(lat, lng)) == true
                }
                ?.toList()
        }

        withContext(Dispatchers.Main) {
            currentJob.ensureActive()
            visibleFlights?.forEach { flight ->
                currentJob.ensureActive()
                val lat = flight.geography?.latitude!!
                val lng = flight.geography.longitude!!
                val direction = flight.geography.direction
                googleMap.addPlaneMarker(lat, lng, markerIcon, direction, flight)
            }
            binding.pg.invisible()
        }
    }

    private var drawMarkersJob: Job? = null

    private var markerIcon: BitmapDescriptor? = null

    private fun getMarkerIcon() {
        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.iv_airplane)
    }
}