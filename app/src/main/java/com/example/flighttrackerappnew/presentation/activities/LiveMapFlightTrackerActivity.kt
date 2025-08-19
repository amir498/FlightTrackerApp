package com.example.flighttrackerappnew.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.db.FollowLiveFlightDao
import com.example.flighttrackerappnew.data.model.FollowFlightData
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.data.model.tracking.TrackedDataItem
import com.example.flighttrackerappnew.databinding.ActivityLiveMapFlightTrackerBinding
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.googleMap.MyGoogleMap
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.favData
import com.example.flighttrackerappnew.presentation.utils.formatTo12HourTime
import com.example.flighttrackerappnew.presentation.utils.getFlightProgressPercent
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.getTimeDifference
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isComeFromFav
import com.example.flighttrackerappnew.presentation.utils.isComeFromTracked
import com.example.flighttrackerappnew.presentation.utils.isFromDetail
import com.example.flighttrackerappnew.presentation.utils.orNA
import com.example.flighttrackerappnew.presentation.utils.selectedLiveFlightData
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.trackData
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.Locale

class LiveMapFlightTrackerActivity :
    BaseActivity<ActivityLiveMapFlightTrackerBinding>(ActivityLiveMapFlightTrackerBinding::inflate) {
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
    private var airLinesList = listOf<StaticAirLineItems>()
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var airportsDataList = listOf<AirportsDataItems>()
    private var citiesList = listOf<CitiesDataItems>()
    private var airPlanesList = listOf<AirPlaneItems>()
    private val followLiveFlightDao: FollowLiveFlightDao by inject()
    private var followFlightData: FollowFlightData? = null
    private val dataCollector: DataCollector by inject()
    private val handler = Handler(Looper.getMainLooper())
    private val rewardedAd: RewardedAdManager by inject()

    private val bannerAdManager: BannerAdManager by inject()
    private lateinit var mBottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.backBtn.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.backBtn.layoutParams = params

        isComeFromFav = false
        isComeFromTracked = false
        trackData = null
        favData = null
        getBottomSheetReference()
        bottomSheetListener()
        val runnable = Runnable {
            getMarkerIcon()
            observeLiveData()
            initView()
            viewListener()
        }

        handler.postDelayed(runnable, 1000)

        val BANNER_LIVE_MAP =
            RemoteConfigManager.getBoolean("BANNER_LIVE_MAP")
        if (BANNER_LIVE_MAP) {
            binding.adContainerView.visible()
            bannerAdManager.loadAd(true, this, app.getString(R.string.BANNER_LIVE_MAP), {
                bannerAdManager.showBannerAd(
                    binding.adContainerView,
                    this@LiveMapFlightTrackerActivity,
                    null
                )
            }, {})
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFollowFlightData()
    }

    private fun showRewardedAd() {
        startActivity(
            Intent(
                this@LiveMapFlightTrackerActivity,
                DetailActivity::class.java
            )
        )
        return
        val REWARDED_LIVE =
            RemoteConfigManager.getBoolean("REWARDED_LIVE")
        if (REWARDED_LIVE) {
            val app = (this as? BaseActivity<*>)?.app
            app?.let {
                rewardedAd.loadAndShowRewardedAd(
                    this,
                    app.getString(R.string.REWARDED_LIVE),
                    onRewardEarned = {
                        startActivity(
                            Intent(
                                this@LiveMapFlightTrackerActivity,
                                DetailActivity::class.java
                            )
                        )
                    }, {
                        startActivity(
                            Intent(
                                this@LiveMapFlightTrackerActivity,
                                DetailActivity::class.java
                            )
                        )
                    }
                )
            }
        }
    }

    private fun viewListener() {
        binding.apply {
            currentLocationBtn.setOnClickListener {
                if (isLocationPermissionGranted()) {
                    googleMap.moveCameraToCurrentLocation(this@LiveMapFlightTrackerActivity)
                } else {
                    requestLocationPermission()
                }
            }
            backBtn.setOnClickListener {
                this@LiveMapFlightTrackerActivity.finish()
            }
            include.conDetails.setOnClickListener {
                showRewardedAd()

            }
            include.consFollow.setOnClickListener {
                if (binding.include.tvFollow.text == ContextCompat.getString(
                        this@LiveMapFlightTrackerActivity,
                        R.string.unfollow
                    )
                ) {
                    isComeFromTracked = false
                    this@LiveMapFlightTrackerActivity.showToast("Flight is not being Followed")
                    binding.include.tvFollow.text =
                        ContextCompat.getString(this@LiveMapFlightTrackerActivity, R.string.follow)
                    if (followFlightData != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            followLiveFlightDao.deleteFollowFlightByNumber(followFlightData!!.flightNum.toString())
                        }
                    }
                } else {
                    isComeFromTracked = true
                    this@LiveMapFlightTrackerActivity.showToast("Flight is being Followed")
                    binding.include.tvFollow.text = ContextCompat.getString(
                        this@LiveMapFlightTrackerActivity,
                        R.string.unfollow
                    )
                    if (followFlightData != null) {
                        trackData = followFlightData
                        lifecycleScope.launch(Dispatchers.IO) {
                            followLiveFlightDao.insertFollowLiveFlightData(followFlightData!!)
                            followLiveFlightDao.getFollowLiveFlightData()
                        }
                    }
                }
            }
        }
    }

    private var followedFlightList = listOf<FollowFlightData>()
    private fun observeLiveData() {
        viewModel.apply {
            followFlightData.observe(this@LiveMapFlightTrackerActivity) { result ->
                followedFlightList = result
            }
            airPlanesData.observe(this@LiveMapFlightTrackerActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airPlanesList = result.data
                    }

                    is Resource.Error -> {
                    }
                }
            }
            airPortsData.observe(this@LiveMapFlightTrackerActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airportsDataList = result.data
                    }

                    is Resource.Error -> {}
                }
            }

            staticAirLineData.observe(this@LiveMapFlightTrackerActivity) { response ->
                when (response) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airLinesList = response.data
                    }

                    is Resource.Error -> {
                    }
                }
            }
            liveFlightData.observe(this@LiveMapFlightTrackerActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        getStaticAirLines()
                        getScheduleFlight()
                        liveFlight = result.data
                        drawMarkersJob = lifecycleScope.launch {
                            delay(2000)
                            setAirplanesData(coroutineContext[Job]!!)
                            delay(2000)
                            if (isFromDetail) {
                                try {
                                    val selectedFlight =
                                        liveFlight?.filter { selectedLiveFlightData?.flightNo == it.flight?.iataNumber }
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
                                            this@LiveMapFlightTrackerActivity,
                                            arrMarkerIcon,
                                            depMarkerIcon,
                                            airplaneSelectedIcon,
                                            airplaneDefaultIcon
                                        )
                                    }
                                } catch (e: IndexOutOfBoundsException) {
                                    e.printStackTrace()
                                }
                            } else {
                                googleMap.zoomAtCurrentLocation()
                            }
                        }

                        googleMap.onCameraIdle { newVisibleBounds ->
                            binding.pg.visible()
                            drawMarkersJob?.cancel()
                            drawMarkersJob = lifecycleScope.launch {
                                delay(1000)
                                setAirplanesData(coroutineContext[Job]!!)
                                binding.pg.invisible()
                            }
                        }

                        googleMap.setOnCameraMoveStartedListener { reason ->
                            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                                drawMarkersJob?.cancel()
                            }
                        }
                    }

                    is Resource.Error -> {
                        Log.d("error", "Error:${result.message} ")
                        this@LiveMapFlightTrackerActivity.showToast("Error: ${result.message}")
                    }
                }
            }

            scheduleFlightData.observe(this@LiveMapFlightTrackerActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        dataCollector.schedules = result.data
                        scheduleFlightList = result.data
                    }

                    is Resource.Error -> {
                        this@LiveMapFlightTrackerActivity.showToast("Error: ${result.message}")
                    }
                }
            }
            citiesData.observe(this@LiveMapFlightTrackerActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        citiesList = result.data
                    }

                    is Resource.Error -> {
                        this@LiveMapFlightTrackerActivity.showToast("Error: ${result.message}")
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
            liveFlight
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

            val visibleFlightIds =
                visibleFlights?.mapNotNull { it.flight?.iataNumber }?.toSet() ?: emptySet()
            val iterator = googleMap.getPlaneMarkers().iterator()
            while (iterator.hasNext()) {
                val (id, marker) = iterator.next()
                if (id !in visibleFlightIds) {
                    marker.remove()
                    iterator.remove()
                }
            }

            visibleFlights?.forEach { flight ->
                currentJob.ensureActive()

                val lat = flight.geography?.latitude ?: return@forEach
                val lng = flight.geography.longitude ?: return@forEach
                val direction = flight.geography.direction
                val id = flight.flight?.iataNumber ?: return@forEach

                if (!googleMap.getPlaneMarkers().containsKey(id)) {
                    googleMap.addPlaneMarker(
                        lat,
                        lng,
                        airplaneDefaultIcon,
                        direction,
                        flight,
                        airplaneSelectedIcon
                    )
                }
            }
        }
    }

    private fun initView() {
        try {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.google_map_fragment) as SupportMapFragment
            googleMap.apply {
                setMapUi(mapFragment)
                listener { flightData ->
                    if (followedFlightList.any { it.flightNum == flightData.flight?.iataNumber }) {
                        binding.include.tvFollow.text = ContextCompat.getString(
                            this@LiveMapFlightTrackerActivity,
                            R.string.unfollow
                        )
                    } else {
                        binding.include.tvFollow.text = ContextCompat.getString(
                            this@LiveMapFlightTrackerActivity,
                            R.string.follow
                        )
                    }
                    try {
                        depAirport =
                            airportsDataList.filter { it.codeIataAirport == flightData.departure?.iataCode } as ArrayList<AirportsDataItems>?
                        arvAirport =
                            airportsDataList.filter { it.codeIataAirport == flightData.arrival?.iataCode } as ArrayList<AirportsDataItems>?

                        drawFlightPathIfNotExists(
                            flightData,
                            depAirport?.get(0),
                            arvAirport?.get(0),
                            this@LiveMapFlightTrackerActivity,
                            arrMarkerIcon,
                            depMarkerIcon,
                            airplaneSelectedIcon,
                            airplaneDefaultIcon
                        )
                        setData(
                            flightData,
                            depAirport?.get(0),
                            arvAirport?.get(0),
                            airLinesList,
                            citiesList,
                            scheduleFlightList,
                            airPlanesList
                        )
                    } catch (e: IndexOutOfBoundsException) {
                        this@LiveMapFlightTrackerActivity.showToast("No Flight Data Found")
                    }
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun bottomSheetListener() {
        mBottomSheetBehaviour.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.adContainerView.visible()
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {}

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {}

                    BottomSheetBehavior.STATE_SETTLING -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getBottomSheetReference() {
        val bottomSheetParent = binding.include.root
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetParent)
        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN

        binding.include.discreteSeekBar.setOnTouchListener { _, _ -> true }
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

            val time = getTimeDifference(depTime.text.toString(), arriTime.text.toString())
            val progress =
                getFlightProgressPercent(depTime.text.toString(), arriTime.text.toString())

            followFlightData = FollowFlightData(
                id = 0,
                depTime.text.toString(),
                arriTime.text.toString(),
                depCityName.text.toString(),
                arrCityName.text.toString(),
                arrivalIataCode.text.toString(),
                depIataCode.text.toString(),
                SpeedValue.text.toString(),
                AltitudeValue.text.toString(),
                airlineName.text.toString(),
                callSign.text.toString(),
                flightNum.text.toString(),
                AirCraftiataNumber.text.toString(),
                time = time,
                progress = progress
            )
        }

        binding.include.apply {
            trackedData = TrackedDataItem(
                id = 0,
                callSign.text.toString(),
                depIataCode.text.toString(),
                arrivalIataCode.text.toString(),
                flightNum.text.toString(),
                AirCraftiataNumber.text.toString(),
                depTime.text.toString(),
                arriTime.text.toString(),
            )
        }

        setFullDetailListData(flightData)
    }

    private fun setFullDetailListData(flightData: FlightDataItem) {
        var fullArrivalFlightDataDetails: FullDetailFlightData? = null

        val arrAirport = airportsDataList.firstOrNull {
            it.codeIataAirport == flightData.arrival?.iataCode
        }

        val airPlane = airPlanesList.firstOrNull {
            it.codeIataAirline == flightData.airline?.iataCode
        }

        val depAirport = airportsDataList.firstOrNull {
            it.codeIataAirport == flightData.departure?.iataCode
        }

        val scheduleFlight = scheduleFlightList.firstOrNull {
            it.airline?.iataCode == flightData.airline?.iataCode
        }
        val progress =
            getFlightProgressPercent(
                binding.include.depTime.text.toString().orNA(),
                binding.include.arriTime.text.toString().orNA()
            )
        fullArrivalFlightDataDetails = FullDetailFlightData(
            flightNo = binding.include.flightNum.text.toString().orNA(),
            depIataCode = binding.include.depIataCode.text.toString().orNA(),
            arrIataCode = binding.include.arrivalIataCode.text.toString().orNA(),
            arrAirportName = arrAirport?.nameAirport.orNA(),
            depAirportName = depAirport?.nameAirport.orNA(),
            depCity = binding.include.depCityName.text.toString().orNA(),
            arrCity = binding.include.arrCityName.text.toString().orNA(),
            nameAirport = depAirport?.nameAirport.orNA(),
            callSign = binding.include.callSign.text.toString().orNA(),
            scheduledArrTime = binding.include.arriTime.text.toString().orNA(),
            scheduledDepTime = binding.include.depTime.text.toString().orNA(),
            actualDepTime = binding.include.depTime.text.toString().orNA(),
            estimatedArrTime = binding.include.arriTime.text.toString().orNA(),
            flightIataNumber = scheduleFlight?.flight?.iataNumber.orNA(),
            airlineName = binding.include.airlineName.text.toString().orNA(),
            flightIcaoNo = scheduleFlight?.arrival?.terminal.orNA(),
            terminal = scheduleFlight?.arrival?.terminal.orNA(),
            gate = scheduleFlight?.arrival?.gate.orNA(),
            delay = scheduleFlight?.arrival?.delay?.toString().orNA(),
            scheduled = flightData.geography?.latitude?.toString().orNA(),
            altitude = flightData.geography?.altitude?.toString().orNA(),
            direction = flightData.geography?.direction?.toString().orNA(),
            latitude = flightData.geography?.latitude?.toString().orNA(),
            longitude = flightData.geography?.longitude?.toString().orNA(),
            hSpeed = flightData.speed?.vspeed?.toString().orNA(),
            vSpeed = flightData.speed?.horizontal?.toString().orNA(),
            status = flightData.status.orNA(),
            squawk = flightData.system?.squawk.orNA(),
            modelName = airPlane?.productionLine.orNA(),
            modelCode = airPlane?.modelCode.orNA(),
            airCraftType = airPlane?.enginesType.orNA(),
            regNo = airPlane?.numberRegistration.orNA(),
            iataModel = airPlane?.airplaneIataType.orNA(),
            icaoHex = airPlane?.hexIcaoAirplane.orNA(),
            productionLine = airPlane?.productionLine.orNA(),
            series = airPlane?.planeSeries.orNA(),
            lineNumber = airPlane?.lineNumber.orNA(),
            constructionNo = airPlane?.constructionNumber.orNA(),
            firstFlight = airPlane?.firstFlight.orNA(),
            deliveryDate = airPlane?.deliveryDate.orNA(),
            rolloutDate = airPlane?.rolloutDate.orNA(),
            currentOwner = airPlane?.planeOwner.orNA(),
            planeStatus = airPlane?.planeStatus.orNA(),
            airLineIataCode = airPlane?.codeIataAirline.orNA(),
            airLineICaoCode = airPlane?.codeIcaoAirline.orNA(),
            airPlaneIataCode = airPlane?.codeIataPlaneLong.orNA(),
            engineCount = airPlane?.enginesCount?.toString().orNA(),
            regDate = airPlane?.registrationDate.orNA(),
            progress = progress
        )

        FullDetailsFlightData = fullArrivalFlightDataDetails
    }

    private var trackedData: TrackedDataItem? = null

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

    override fun onDestroy() {
        super.onDestroy()
//        lastSelectedPlane = null
    }
}