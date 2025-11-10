package com.example.flighttrackerappnew.presentation.activities.main

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.databinding.ActivityLiveMapFlightTrackerForRouteBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_LIVE_MAP
import com.example.flighttrackerappnew.presentation.googleMap.MyGoogleMap
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.formatTo12HourTime
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Locale

class LiveMapDetailRouteActivity : BaseActivity<ActivityLiveMapFlightTrackerForRouteBinding>(
    ActivityLiveMapFlightTrackerForRouteBinding::inflate
) {

    private var airplaneDefaultIcon: BitmapDescriptor? = null
    private var airplaneSelectedIcon: BitmapDescriptor? = null
    private var arrMarkerIcon: BitmapDescriptor? = null
    private var depMarkerIcon: BitmapDescriptor? = null

    private val viewModel: FlightAppViewModel by inject()
    private val googleMap: MyGoogleMap by inject()
    private var drawMarkersJob: Job? = null
    private var liveFlight: List<FlightDataItem>? = null
    private var depAirport: ArrayList<AirportsDataItems>? = null
    private var arvAirport: ArrayList<AirportsDataItems>? = null
    private var airportsDataList = listOf<AirportsDataItems>()
    private var airLinesList = listOf<StaticAirLineItems>()
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var airPlanesList = listOf<AirPlaneItems>()
    private var citiesList = listOf<CitiesDataItems>()

    private lateinit var mBottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getBottomSheetReference()
        bottomSheetListener()
        getMarkerIcon()
        observeLiveData()
        initView()
        loadBannerAd()

        viewListener()
    }

    private fun bottomSheetListener() {
        mBottomSheetBehaviour.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {}

                    BottomSheetBehavior.STATE_COLLAPSED -> {}

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {}

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {}

                    BottomSheetBehavior.STATE_SETTLING -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    fun setData(
        flightData: FlightDataItem,
        depAirport: AirportsDataItems?,
        arvAirport: AirportsDataItems?,
        airLinesList: List<StaticAirLineItems>,
        citiesList: List<CitiesDataItems>,
        scheduleFlightList: List<FlightSchedulesItems>,
        airPlanesList: List<AirPlaneItems>
    ) {
        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        binding.adContainerView.invisible()
        binding.include.apply {
            AirCraftiataNumber.text = flightData.aircraft?.iataCode
            flightNum.text = flightData.flight?.iataNumber
            callSign.text = flightData.flight?.icaoNumber

            if (flightData.status == "en-route") {
                status.visible()
            } else {
                status.invisible()
            }

            val airLine =
                airLinesList.firstOrNull { it.codeIataAirline == flightData.airline?.iataCode }
            airlineName.text = airLine?.nameAirline ?: "N/A"

            val airPlane =
                airPlanesList.firstOrNull { it.codeIataAirline == flightData.airline?.iataCode }
            AirCarftName.text = airPlane?.productionLine ?: "N/A"

            val altitudeFeet = flightData.geography?.altitude?.times(3.28084)
            AltitudeValue.text = if (altitudeFeet != null) {
                String.format(Locale.US, "%,d ft", altitudeFeet.toInt())
            } else {
                "N/A"
            }

            val speedKmh = flightData.speed?.horizontal
            SpeedValue.text = if (speedKmh != null) {
                String.format(Locale.US, "%,d km/h", speedKmh.toInt())
            } else {
                "N/A"
            }

            val deptIataCode = flightData.departure?.iataCode
            depIataCode.text = deptIataCode

            val arriIataCode = flightData.arrival?.iataCode
            arrivalIataCode.text = arriIataCode

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arvAirport?.codeIataCity

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            depCityName.text = depCity?.nameCity ?: "N/A"
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }
            arrCityName.text = arrCity?.nameCity ?: "N/A"
            val scheduleFlight = scheduleFlightList.firstOrNull {
                it.airline?.iataCode == flightData.airline?.iataCode
            }
            depTime.text = formatTo12HourTime(scheduleFlight?.departure?.actualTime ?: "N/A")
            arriTime.text = formatTo12HourTime(scheduleFlight?.arrival?.estimatedTime ?: "N/A")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getBottomSheetReference() {
        val bottomSheetParent = binding.include.root
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetParent)

        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.include.discreteSeekBar.setOnTouchListener { _, _ -> true }
    }

    private fun viewListener() {
        binding.apply {
            backBtn.setOnClickListener {
                finish()
            }
        }
    }

    private fun loadBannerAd() {
        BANNER_LIVE_MAP.apply {
            loadAndShowBannerAd(
                context = this@LiveMapDetailRouteActivity,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {})
        }
    }

    private fun observeLiveData() {
        viewModel.apply {
            staticAirLineData.observe(this@LiveMapDetailRouteActivity) { response ->
                when (response) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airLinesList = response.data
                    }

                    is Resource.Error -> {
                    }
                }
            }
            scheduleFlightData.observe(this@LiveMapDetailRouteActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        scheduleFlightList = result.data
                    }

                    is Resource.Error -> {
                        this@LiveMapDetailRouteActivity.showToast("Error: ${result.message}")
                    }
                }
            }
            citiesData.observe(this@LiveMapDetailRouteActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        citiesList = result.data
                    }

                    is Resource.Error -> {
                        this@LiveMapDetailRouteActivity.showToast("Error: ${result.message}")
                    }
                }
            }
            airCraftData.observe(this@LiveMapDetailRouteActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airPlanesList = result.data
                    }

                    is Resource.Error -> {
                    }
                }
            }

            airPortsData.observe(this@LiveMapDetailRouteActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airportsDataList = result.data
                    }

                    is Resource.Error -> {}
                }
            }

            liveFlightData.observe(this@LiveMapDetailRouteActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        getStaticAirLines()
                        getScheduleFlight()
                        liveFlight = result.data
                        drawMarkersJob = lifecycleScope.launch {
                            delay(2000)
                            try {
                                val selectedFlight =
                                    liveFlight?.filter { FullDetailsFlightData?.flightNo == it.flight?.iataNumber }
                                        ?.get(0)
                                depAirport =
                                    airportsDataList.filter { it.codeIataAirport == selectedFlight?.departure?.iataCode } as ArrayList<AirportsDataItems>?
                                arvAirport =
                                    airportsDataList.filter { it.codeIataAirport == selectedFlight?.arrival?.iataCode } as ArrayList<AirportsDataItems>?
                                if (selectedFlight != null) {
                                    googleMap.setSelectedFlight(selectedFlight)
                                    googleMap.zoomAtSelectedPlane()
                                    googleMap.drawFlightPathIfNotExists(
                                        selectedFlight,
                                        depAirport?.get(0),
                                        arvAirport?.get(0),
                                        this@LiveMapDetailRouteActivity,
                                        arrMarkerIcon,
                                        depMarkerIcon,
                                        airplaneSelectedIcon,
                                        airplaneDefaultIcon
                                    )
                                    setData(
                                        selectedFlight,
                                        depAirport?.get(0),
                                        arvAirport?.get(0),
                                        airLinesList,
                                        citiesList,
                                        scheduleFlightList,
                                        airPlanesList
                                    )
                                } else {
                                    this@LiveMapDetailRouteActivity.showToast("No Route Found")
                                }
                            } catch (e: IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    is Resource.Error -> {
                        Log.d("error", "Error:${result.message} ")
                        this@LiveMapDetailRouteActivity.showToast("Error: ${result.message}")
                    }
                }
            }
        }
    }

    private fun initView() {
        try {
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.google_map_fragment) as? SupportMapFragment

            if (mapFragment == null) {
                return
            }

            googleMap.apply {
                setMapUi(mapFragment)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun getMarkerIcon() {
        val airPlaneDrawable = ContextCompat.getDrawable(this, R.drawable.iv_airplane) ?: return
        val airPlaneDrawableSelected =
            ContextCompat.getDrawable(this, R.drawable.iv_selected_airplane) ?: return
        val depDrawable =
            ContextCompat.getDrawable(this, R.drawable.departure_map_marker_n) ?: return
        val arrDrawable = ContextCompat.getDrawable(this, R.drawable.arrival_map_marker_n) ?: return

        val width = 80
        val height = 80

        val widthM = 40
        val heightM = 40

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        airPlaneDrawable.setBounds(0, 0, width, height)
        airPlaneDrawable.draw(canvas)

        val bitmapS = createBitmap(width, height)
        val canvasS = Canvas(bitmapS)

        airPlaneDrawableSelected.setBounds(0, 0, width, height)
        airPlaneDrawableSelected.draw(canvasS)

        val bitmapArr = createBitmap(widthM, heightM)
        val canvasArr = Canvas(bitmapArr)

        arrDrawable.setBounds(0, 0, widthM, heightM)
        arrDrawable.draw(canvasArr)

        val bitmapDep = createBitmap(widthM, heightM)
        val canvasDep = Canvas(bitmapDep)

        depDrawable.setBounds(0, 0, widthM, heightM)
        depDrawable.draw(canvasDep)

        airplaneDefaultIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
        airplaneSelectedIcon = BitmapDescriptorFactory.fromBitmap(bitmapS)
        arrMarkerIcon = BitmapDescriptorFactory.fromBitmap(bitmapArr)
        depMarkerIcon = BitmapDescriptorFactory.fromBitmap(bitmapDep)
    }
}