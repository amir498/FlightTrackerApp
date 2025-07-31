package com.example.liveflighttrackerapp.presentation.activities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.liveflighttrackerapp.R
import com.example.liveflighttrackerapp.data.db.TrackedFlightDao
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.model.arrival.ArrivalDataItems
import com.example.liveflighttrackerapp.data.model.cities.CitiesDataItems
import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.data.model.schedulesFlight.SchedulesFlightsItems
import com.example.liveflighttrackerapp.data.model.tracking.TrackedDataItem
import com.example.liveflighttrackerapp.databinding.ActivityMainBinding
import com.example.liveflighttrackerapp.presentation.adapter.MainActivityViewPagerAdapter
import com.example.liveflighttrackerapp.presentation.adapter.SearchAirCraftsAdapter
import com.example.liveflighttrackerapp.presentation.adapter.SearchAirLinesAdapter
import com.example.liveflighttrackerapp.presentation.adapter.SearchAirportAdapter
import com.example.liveflighttrackerapp.presentation.adapter.TailSearchAdapter
import com.example.liveflighttrackerapp.presentation.fragments.MyFragment
import com.example.liveflighttrackerapp.presentation.gesturedetector.MyGestureListener
import com.example.liveflighttrackerapp.presentation.listener.FlingListener
import com.example.liveflighttrackerapp.presentation.sealedClasses.Resource
import com.example.liveflighttrackerapp.presentation.utils.arrivalFlightData
import com.example.liveflighttrackerapp.presentation.utils.departureFlightData
import com.example.liveflighttrackerapp.presentation.utils.formatTo12HourTime
import com.example.liveflighttrackerapp.presentation.utils.getStatusBarHeight
import com.example.liveflighttrackerapp.presentation.utils.hideSystemUI
import com.example.liveflighttrackerapp.presentation.utils.invisible
import com.example.liveflighttrackerapp.presentation.utils.logDebug
import com.example.liveflighttrackerapp.presentation.utils.realScreenSize
import com.example.liveflighttrackerapp.presentation.utils.setScreenDisplay
import com.example.liveflighttrackerapp.presentation.utils.showSystemUI
import com.example.liveflighttrackerapp.presentation.utils.visible
import com.example.liveflighttrackerapp.presentation.utils.visibleFragmentName
import com.example.liveflighttrackerapp.presentation.viewmodels.FlightAppViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity(), FlingListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var mBottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>
    private var mScreenHeight = 0
    private var statusBarHeight: Int = 0

    private val viewModel: FlightAppViewModel by inject()
    var airportsDataList = listOf<AirportsDataItems>()
    var liveFlight = listOf<FlightDataItem>()
    var airLines = listOf<StaticAirLineItems>()
    private var airPlane = listOf<AirPlaneItems>()
    private var citiesList = listOf<CitiesDataItems>()
    private var mDetector: GestureDetector? = null

    private var searchAirportAdapter: SearchAirportAdapter? = null
    private var aircraftSearchAdapter: SearchAirCraftsAdapter? = null
    private var airlinesSearchAirLinesAdapter: SearchAirLinesAdapter? = null
    private var tailSearchAdapter: TailSearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNav)) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        window.apply {
            statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.white)
            navigationBarColor =
                ContextCompat.getColor(this@MainActivity, R.color.con_background_color)
        }
        setScreenDisplay()
        window.hideSystemUI()
        mScreenHeight = realScreenSize.y

        getBottomSheetReference()
        binding.bottomNav.itemIconTintList = null

        statusBarHeight = this@MainActivity.getStatusBarHeight

        setUpAllFragments()
        setUpRecyclerView()
        registeredBackPress()
        observerLiveData()
        bottomSheetListener()
        initViewPagerAndBottomNavigationListener()
        mDetector = GestureDetector(this, MyGestureListener(this))
        globallyTreeObserver()
        viewListener()
    }

    private fun viewListener() {
        binding.include.apply {
            consFollow.setOnClickListener {
                trackedData?.let {
                    lifecycleScope.launch(Dispatchers.IO) {
                        trackingFlightDao.insertTrackedFlightData(it)
                    }
                }
            }
            consRoute.setOnClickListener {
                trackedData?.let {
                    lifecycleScope.launch(Dispatchers.IO) {
                        trackingFlightDao.insertTrackedFlightData(it)
                    }
                }
            }
            consShare.setOnClickListener {
                trackedData?.let {
                    lifecycleScope.launch(Dispatchers.IO) {
                        trackingFlightDao.insertTrackedFlightData(it)
                    }
                }
            }
            consDetail.setOnClickListener {
                trackedData?.let {
                    lifecycleScope.launch(Dispatchers.IO) {
                        trackingFlightDao.insertTrackedFlightData(it)
                    }
                }
            }
        }
    }

    private fun globallyTreeObserver() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.rootView.height
            val keypadHeight = screenHeight - r.bottom

            val isKeyboardOpen = keypadHeight > screenHeight * 0.15
            if (isKeyboardOpen) {
                window.showSystemUI()
            } else {
                window.hideSystemUI()
            }
        }
    }

    private fun setUpRecyclerView() {
        searchAirportAdapter = SearchAirportAdapter()
        aircraftSearchAdapter = SearchAirCraftsAdapter()
        airlinesSearchAirLinesAdapter = SearchAirLinesAdapter()
        tailSearchAdapter = TailSearchAdapter()
        binding.apply {
            fragmentSearchAirCrafts.recyclerView.adapter = aircraftSearchAdapter
            fragmentSearchAirLine.recyclerView.adapter = airlinesSearchAirLinesAdapter
            fragmentSearchAirport.recyclerView.adapter = searchAirportAdapter
            fragmentSearchTail.recyclerView.adapter = tailSearchAdapter
        }
    }

    private fun observerLiveData() {
        viewModel.apply {
            airPortsData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airportsDataList = result.data
                        val airportIataCodes: Set<String> = buildSet {
                            liveFlight.mapNotNullTo(this) { it.arrival?.iataCode?.lowercase() }
                        }
                        val matchedAirports = airportsDataList.filter { airport ->
                            airport.codeIataAirport?.lowercase() in airportIataCodes
                        }

                        searchAirportAdapter?.let {
                            it.setList(matchedAirports)
                            it.setListener { airPortDetail ->
                                binding.fragmentAirPortSearch.pg.visible()
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val job = lifecycleScope.launch(Dispatchers.IO) {
                                        arrivalFlightData =
                                            getArrivalFlightDataFromAirport(airPortDetail)
                                        departureFlightData =
                                            getDepartureFlightDataFromAirport(airPortDetail)
                                    }
                                    job.join()
                                    withContext(Dispatchers.Main) {
                                        binding.fragmentAirPortSearch.pg.invisible()
                                        binding.fragmentAirPortSearch.root.setData(
                                            ContextCompat.getString(
                                                this@MainActivity, R.string.airport_search
                                            ), true
                                        )
                                    }
                                }
                                showFragment(binding.fragmentAirPortSearch)

                            }
                        }
                    }

                    is Resource.Error -> {}
                }
            }
            liveFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        viewModel.getAirPorts()
                        liveFlight = result.data
                        aircraftSearchAdapter?.let {
                            it.setList(liveFlight)
                            it.setListener { arrivalFlight ->
                                binding.fragmentAirPortSearch.pg.visible()
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val job = lifecycleScope.launch(Dispatchers.IO) {
                                        arrivalFlightData =
                                            getArrivalFlightDataFromAircraft(arrivalFlight)
                                        departureFlightData =
                                            getDepartureFlightDataFromAircraft(arrivalFlight)
                                    }
                                    job.join()
                                    withContext(Dispatchers.Main) {
                                        binding.fragmentAirPortSearch.pg.invisible()
                                        binding.fragmentAirPortSearch.root.setData(
                                            ContextCompat.getString(
                                                this@MainActivity, R.string.aircraft_track
                                            ), false
                                        )
                                    }
                                }
                                showFragment(binding.fragmentAirPortSearch)
                            }
                        }
                    }

                    is Resource.Error -> {}
                }
            }
            staticAirLineData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airLines = result.data
                        airlinesSearchAirLinesAdapter?.setList(airLines)
                        airlinesSearchAirLinesAdapter?.setListener { airLineData ->
                            binding.fragmentAirPortSearch.pg.visible()
                            lifecycleScope.launch(Dispatchers.IO) {
                                val job = lifecycleScope.launch(Dispatchers.IO) {
                                    arrivalFlightData = getArrivalFlightDataFromAirLine(airLineData)
                                    departureFlightData =
                                        getDepartureFlightDataFromAirline(airLineData)
                                }
                                job.join()
                                withContext(Dispatchers.Main) {
                                    binding.fragmentAirPortSearch.pg.invisible()
                                    binding.fragmentAirPortSearch.root.setData(
                                        ContextCompat.getString(
                                            this@MainActivity, R.string.airline_track
                                        ), false
                                    )
                                }
                            }
                            showFragment(binding.fragmentAirPortSearch)
                        }
                    }

                    is Resource.Error -> {}
                }
            }
            airPlanesData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        airPlane = result.data.filter { it.planeStatus == "active" }
                        val matchingAirplanes = airPlane.filter { airplane ->
                            liveFlight.any { flight ->
                                flight.aircraft?.regNumber?.equals(
                                    airplane.numberRegistration, ignoreCase = true
                                ) == true
                            }
                        }
                        tailSearchAdapter?.setList(matchingAirplanes)
                        tailSearchAdapter?.setListener { tailData: AirPlaneItems ->
                            binding.fragmentAirPortSearch.pg.visible()
                            lifecycleScope.launch(Dispatchers.IO) {
                                val job = lifecycleScope.launch(Dispatchers.IO) {
                                    arrivalFlightData = getArrivalFlightDataFromTailNumber(tailData)
                                    departureFlightData =
                                        getDepartureFlightDataFromTailNumber(tailData)
                                }
                                job.join()
                                withContext(Dispatchers.Main) {
                                    binding.fragmentAirPortSearch.pg.invisible()
                                    binding.fragmentAirPortSearch.root.setData(
                                        ContextCompat.getString(
                                            this@MainActivity, R.string.airline_track
                                        ), false
                                    )
                                }
                            }
                            showFragment(binding.fragmentAirPortSearch)
                        }
                    }

                    is Resource.Error -> {}
                }
            }
            citiesData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        citiesList = result.data
                    }

                    is Resource.Error -> {}
                }
            }
            scheduleFlightData.observe(this@MainActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}

                    is Resource.Success -> {
                        scheduleFlightList = result.data
                    }

                    is Resource.Error -> {
                    }
                }
            }
        }
    }

    fun getArrivalFlightDataFromAirport(airPortDetail: AirportsDataItems): ArrayList<ArrivalDataItems> {
        val arrivalFlightList = liveFlight.filter {
            it.arrival?.iataCode == airPortDetail.codeIataAirport
        }

        val arrivalFlightData = ArrayList<ArrivalDataItems>()

        arrivalFlightList.forEach { arrFlight ->
            val arrAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == arrFlight.arrival?.iataCode
            }

            val depAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == arrFlight.departure?.iataCode
            }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight = scheduleFlightList.firstOrNull {
                it.airline?.iataCode == arrFlight.airline?.iataCode
            }

            val airPlane = airPlane.firstOrNull {
                it.codeIataAirline == arrFlight.airline?.iataCode
            }

            arrivalFlightData.add(
                ArrivalDataItems(
                    arrFlight.flight?.iataNumber.orEmpty(),
                    arrFlight.departure?.iataCode.orEmpty(),
                    arrFlight.arrival?.iataCode.orEmpty(),
                    arrAirport?.nameAirport.orEmpty(),
                    depAirport?.nameAirport.orEmpty(),
                    arrCity?.nameCity.orEmpty(),
                    depCity?.nameCity.orEmpty(),
                    airPortDetail.nameAirport.orEmpty(),
                    arrFlight.flight?.icaoNumber.orEmpty(),
                    scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orEmpty(),
                    scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orEmpty(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                    estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime.orEmpty(),
                    flightIataNumber = arrFlight.flight?.iataNumber.orEmpty(),
                    airlineName = scheduleFlight?.airline?.name.orEmpty(),
                    flightIcaoNo = arrFlight.flight?.icaoNumber.orEmpty(),
                    terminal = scheduleFlight?.arrival?.terminal.orEmpty(),
                    gate = scheduleFlight?.arrival?.gate.orEmpty(),
                    delay = scheduleFlight?.departure?.delay?.toString().orEmpty(),
                    scheduled = scheduleFlight?.departure?.scheduledTime.orEmpty(),
                    altitude = arrFlight.geography?.altitude?.toString().orEmpty(),
                    direction = arrFlight.geography?.direction?.toString().orEmpty(),
                    latitude = arrFlight.geography?.latitude?.toString().orEmpty(),
                    longitude = arrFlight.geography?.longitude?.toString().orEmpty(),
                    hSpeed = arrFlight.speed?.horizontal?.toString().orEmpty(),
                    vSpeed = arrFlight.speed?.vspeed?.toString().orEmpty(),
                    status = arrFlight.status.orEmpty(),
                    squawk = arrFlight.system?.squawk.orEmpty(),
                    modelName = airPlane?.productionLine.orEmpty(),
                    modelCode = airPlane?.modelCode.orEmpty(),
                    airCraftType = airPlane?.enginesType.orEmpty(),
                    regNo = airPlane?.numberRegistration.orEmpty(),
                    iataModel = airPlane?.airplaneIataType.orEmpty(),
                    icaoHex = airPlane?.hexIcaoAirplane.orEmpty(),
                    productionLine = airPlane?.productionLine.orEmpty(),
                    series = airPlane?.planeSeries.orEmpty(),
                    lineNumber = airPlane?.lineNumber.orEmpty(),
                    constructionNo = airPlane?.constructionNumber.orEmpty(),
                    firstFlight = airPlane?.firstFlight.orEmpty(),
                    deliveryDate = airPlane?.deliveryDate.orEmpty(),
                    rolloutDate = airPlane?.rolloutDate.orEmpty(),
                    currentOwner = airPlane?.planeOwner.orEmpty(),
                    planeStatus = airPlane?.planeStatus.orEmpty(),
                    airLineIataCode = airPlane?.codeIataAirline.orEmpty(),
                    airLineICaoCode = airPlane?.codeIcaoAirline.orEmpty()
                )
            )
        }

        return arrivalFlightData
    }

    fun getArrivalFlightDataFromAirLine(airLineDetail: StaticAirLineItems): ArrayList<ArrivalDataItems> {
        val arrivalFlightList: List<FlightDataItem> = liveFlight.filter {
            it.airline?.iataCode == airLineDetail.codeIataAirline && it.status == "en-route"
        }

        val arrivalFlightData = ArrayList<ArrivalDataItems>()
        arrivalFlightList.forEach { arrFlight ->
            val arrAirport =
                airportsDataList.firstOrNull { it.codeIataAirport == arrFlight.arrival?.iataCode }
            val depAirport =
                airportsDataList.firstOrNull { it.codeIataAirport == arrFlight.departure?.iataCode }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val arrAirportName = arrAirport?.nameAirport ?: "Unknown"
            val depAirportName = depAirport?.nameAirport ?: "Unknown"

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight: SchedulesFlightsItems? =
                scheduleFlightList.firstOrNull { it.airline?.iataCode == arrFlight.airline?.iataCode }

            val airPlane: AirPlaneItems? =
                airPlane.firstOrNull { it.codeIataAirline == arrFlight.airline?.iataCode }

            arrivalFlightData.add(
                ArrivalDataItems(
                    arrFlight.flight?.iataNumber.toString(),
                    arrFlight.departure?.iataCode.toString(),
                    arrFlight.arrival?.iataCode.toString(),
                    arrAirportName.toString(),
                    depAirportName.toString(),
                    arrCity?.nameCity.toString(),
                    depCity?.nameCity.toString(),
                    "",
                    arrFlight.flight?.icaoNumber.toString(),
                    scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.toString(),
                    scheduledDepTime = scheduleFlight?.departure?.scheduledTime.toString(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.toString(),
                    estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime.toString(),
                    flightIataNumber = arrFlight.flight?.iataNumber.toString(),
                    airlineName = scheduleFlight?.airline?.name.toString(),
                    flightIcaoNo = arrFlight.flight?.icaoNumber.toString(),
                    terminal = scheduleFlight?.arrival?.terminal.toString(),
                    gate = scheduleFlight?.arrival?.gate.toString(),
                    delay = scheduleFlight?.departure?.delay.toString(),
                    scheduled = scheduleFlight?.departure?.scheduledTime.toString(),
                    altitude = arrFlight.geography?.altitude.toString(),
                    direction = arrFlight.geography?.direction.toString(),
                    latitude = arrFlight.geography?.latitude.toString(),
                    longitude = arrFlight.geography?.longitude.toString(),
                    hSpeed = arrFlight.speed?.horizontal.toString(),
                    vSpeed = arrFlight.speed?.vspeed.toString(),
                    status = arrFlight.status.toString(),
                    squawk = arrFlight.system?.squawk.toString(),
                    modelName = airPlane?.productionLine.toString(),
                    modelCode = airPlane?.modelCode.toString(),
                    airCraftType = airPlane?.enginesType.toString(),
                    regNo = airPlane?.numberRegistration.toString(),
                    iataModel = airPlane?.airplaneIataType.toString(),
                    icaoHex = airPlane?.hexIcaoAirplane.toString(),
                    productionLine = airPlane?.productionLine.toString(),
                    series = airPlane?.planeSeries.toString(),
                    lineNumber = airPlane?.lineNumber.toString(),
                    constructionNo = airPlane?.constructionNumber.toString(),
                    firstFlight = airPlane?.firstFlight.toString(),
                    deliveryDate = airPlane?.deliveryDate.toString(),
                    rolloutDate = airPlane?.rolloutDate.toString(),
                    currentOwner = airPlane?.planeOwner.toString(),
                    planeStatus = airPlane?.planeStatus.toString(),
                    airLineIataCode = airPlane?.codeIataAirline.toString(),
                    airLineICaoCode = airPlane?.codeIcaoAirline.toString()
                )
            )
        }

        return arrivalFlightData
    }

    fun getArrivalFlightDataFromTailNumber(tailData: AirPlaneItems): ArrayList<ArrivalDataItems> {
        val arrivalFlightList = liveFlight.filter {
            it.aircraft?.regNumber == tailData.numberRegistration
        }

        val arrivalFlightData = ArrayList<ArrivalDataItems>()

        arrivalFlightList.forEach { arrFlight ->
            val arrAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == arrFlight.arrival?.iataCode
            }

            val depAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == arrFlight.departure?.iataCode
            }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight = scheduleFlightList.firstOrNull {
                it.airline?.iataCode == arrFlight.airline?.iataCode
            }

            val airPlane = airPlane.firstOrNull {
                it.codeIataAirline == arrFlight.airline?.iataCode
            }

            arrivalFlightData.add(
                ArrivalDataItems(
                    arrFlight.flight?.iataNumber.orEmpty(),
                    arrFlight.departure?.iataCode.orEmpty(),
                    arrFlight.arrival?.iataCode.orEmpty(),
                    arrAirport?.nameAirport.orEmpty(),
                    depAirport?.nameAirport.orEmpty(),
                    arrCity?.nameCity.orEmpty(),
                    depCity?.nameCity.orEmpty(),
                    "",
                    arrFlight.flight?.icaoNumber.orEmpty(),
                    scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orEmpty(),
                    scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orEmpty(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                    estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime.orEmpty(),
                    flightIataNumber = arrFlight.flight?.iataNumber.orEmpty(),
                    airlineName = scheduleFlight?.airline?.name.orEmpty(),
                    flightIcaoNo = arrFlight.flight?.icaoNumber.orEmpty(),
                    terminal = scheduleFlight?.arrival?.terminal.orEmpty(),
                    gate = scheduleFlight?.arrival?.gate.orEmpty(),
                    delay = scheduleFlight?.departure?.delay?.toString().orEmpty(),
                    scheduled = scheduleFlight?.departure?.scheduledTime.orEmpty(),
                    altitude = arrFlight.geography?.altitude?.toString().orEmpty(),
                    direction = arrFlight.geography?.direction?.toString().orEmpty(),
                    latitude = arrFlight.geography?.latitude?.toString().orEmpty(),
                    longitude = arrFlight.geography?.longitude?.toString().orEmpty(),
                    hSpeed = arrFlight.speed?.horizontal?.toString().orEmpty(),
                    vSpeed = arrFlight.speed?.vspeed?.toString().orEmpty(),
                    status = arrFlight.status.orEmpty(),
                    squawk = arrFlight.system?.squawk.orEmpty(),
                    modelName = airPlane?.productionLine.orEmpty(),
                    modelCode = airPlane?.modelCode.orEmpty(),
                    airCraftType = airPlane?.enginesType.orEmpty(),
                    regNo = airPlane?.numberRegistration.orEmpty(),
                    iataModel = airPlane?.airplaneIataType.orEmpty(),
                    icaoHex = airPlane?.hexIcaoAirplane.orEmpty(),
                    productionLine = airPlane?.productionLine.orEmpty(),
                    series = airPlane?.planeSeries.orEmpty(),
                    lineNumber = airPlane?.lineNumber.orEmpty(),
                    constructionNo = airPlane?.constructionNumber.orEmpty(),
                    firstFlight = airPlane?.firstFlight.orEmpty(),
                    deliveryDate = airPlane?.deliveryDate.orEmpty(),
                    rolloutDate = airPlane?.rolloutDate.orEmpty(),
                    currentOwner = airPlane?.planeOwner.orEmpty(),
                    planeStatus = airPlane?.planeStatus.orEmpty(),
                    airLineIataCode = airPlane?.codeIataAirline.orEmpty(),
                    airLineICaoCode = airPlane?.codeIcaoAirline.orEmpty()
                )
            )
        }

        return arrivalFlightData
    }

    fun getArrivalFlightDataFromAircraft(arrivalFlight: FlightDataItem): ArrayList<ArrivalDataItems> {
        val arrivalFlightData = ArrayList<ArrivalDataItems>()

        val arrAirport = airportsDataList.firstOrNull {
            it.codeIataAirport == arrivalFlight.arrival?.iataCode
        }

        val depAirport = airportsDataList.firstOrNull {
            it.codeIataAirport == arrivalFlight.departure?.iataCode
        }

        val codeIataCityDep = depAirport?.codeIataCity
        val codeIataCityArr = arrAirport?.codeIataCity

        val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
        val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

        val scheduleFlight: SchedulesFlightsItems? = scheduleFlightList.firstOrNull {
            it.airline?.iataCode == arrivalFlight.airline?.iataCode
        }

        val airPlane: AirPlaneItems? = airPlane.firstOrNull {
            it.codeIataAirline == arrivalFlight.airline?.iataCode
        }

        val airPortDetail = airportsDataList.firstOrNull {
            it.codeIataAirport == arrivalFlight.arrival?.iataCode
        }

        arrivalFlightData.add(
            ArrivalDataItems(
                arrivalFlight.flight?.iataNumber.orEmpty(),
                arrivalFlight.departure?.iataCode.orEmpty(),
                arrivalFlight.arrival?.iataCode.orEmpty(),
                arrAirport?.nameAirport.orEmpty(),
                depAirport?.nameAirport.orEmpty(),
                arrCity?.nameCity.orEmpty(),
                depCity?.nameCity.orEmpty(),
                airPortDetail?.nameAirport.orEmpty(),
                arrivalFlight.flight?.icaoNumber.orEmpty(),
                scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orEmpty(),
                scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orEmpty(),
                actualDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime.orEmpty(),
                flightIataNumber = arrivalFlight.flight?.iataNumber.orEmpty(),
                airlineName = scheduleFlight?.airline?.name.orEmpty(),
                flightIcaoNo = arrivalFlight.flight?.icaoNumber.orEmpty(),
                terminal = scheduleFlight?.arrival?.terminal.orEmpty(),
                gate = scheduleFlight?.arrival?.gate.orEmpty(),
                delay = scheduleFlight?.departure?.delay.orEmpty(),
                scheduled = scheduleFlight?.departure?.scheduledTime.orEmpty(),
                altitude = arrivalFlight.geography?.altitude.toString(),
                direction = arrivalFlight.geography?.direction.toString(),
                latitude = arrivalFlight.geography?.latitude.toString(),
                longitude = arrivalFlight.geography?.longitude.toString(),
                hSpeed = arrivalFlight.speed?.horizontal.toString(),
                vSpeed = arrivalFlight.speed?.vspeed.toString(),
                status = arrivalFlight.status.orEmpty(),
                squawk = arrivalFlight.system?.squawk.orEmpty(),
                modelName = airPlane?.productionLine.orEmpty(),
                modelCode = airPlane?.modelCode.orEmpty(),
                airCraftType = airPlane?.enginesType.orEmpty(),
                regNo = airPlane?.numberRegistration.orEmpty(),
                iataModel = airPlane?.airplaneIataType.orEmpty(),
                icaoHex = airPlane?.hexIcaoAirplane.orEmpty(),
                productionLine = airPlane?.productionLine.orEmpty(),
                series = airPlane?.planeSeries.orEmpty(),
                lineNumber = airPlane?.lineNumber.orEmpty(),
                constructionNo = airPlane?.constructionNumber.orEmpty(),
                firstFlight = airPlane?.firstFlight.orEmpty(),
                deliveryDate = airPlane?.deliveryDate.orEmpty(),
                rolloutDate = airPlane?.rolloutDate.orEmpty(),
                currentOwner = airPlane?.planeOwner.orEmpty(),
                planeStatus = airPlane?.planeStatus.orEmpty(),
                airLineIataCode = airPlane?.codeIataAirline.orEmpty(),
                airLineICaoCode = airPlane?.codeIcaoAirline.orEmpty()
            )
        )

        return arrivalFlightData
    }

    fun getDepartureFlightDataFromAirport(airPortDetail: AirportsDataItems): ArrayList<ArrivalDataItems> {
        val departureFlightList: List<FlightDataItem> =
            liveFlight.filter { it.departure?.iataCode == airPortDetail.codeIataCity }

        val departureFlightData = ArrayList<ArrivalDataItems>()
        departureFlightList.forEach { depFlight ->
            val arrAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == depFlight.arrival?.iataCode
            }
            val depAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == depFlight.departure?.iataCode
            }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight = scheduleFlightList.firstOrNull {
                it.airline?.iataCode == depFlight.airline?.iataCode
            }

            val airPlane: AirPlaneItems? = airPlane.firstOrNull {
                it.codeIataAirline == depFlight.airline?.iataCode
            }

            departureFlightData.add(
                ArrivalDataItems(
                    depFlight.flight?.iataNumber.orEmpty(),
                    depFlight.departure?.iataCode.orEmpty(),
                    depFlight.arrival?.iataCode.orEmpty(),
                    arrAirport?.nameAirport.orEmpty(),
                    depAirport?.nameAirport.orEmpty(),
                    arrCity?.nameCity.orEmpty(),
                    depCity?.nameCity.orEmpty(),
                    airPortDetail.nameAirport.orEmpty(),
                    depFlight.flight?.icaoNumber.orEmpty(),
                    scheduledArrTime = scheduleFlight?.arrival?.actualTime.orEmpty(),
                    scheduledDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                    estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime.orEmpty(),
                    flightIataNumber = depFlight.flight?.iataNumber.orEmpty(),
                    airlineName = scheduleFlight?.airline?.name.orEmpty(),
                    flightIcaoNo = depFlight.flight?.icaoNumber.orEmpty(),
                    terminal = scheduleFlight?.arrival?.terminal.orEmpty(),
                    gate = scheduleFlight?.departure?.gate.orEmpty(),
                    delay = scheduleFlight?.departure?.delay?.toString().orEmpty(),
                    scheduled = scheduleFlight?.departure?.scheduledTime.orEmpty(),
                    altitude = depFlight.geography?.altitude?.toString().orEmpty(),
                    direction = depFlight.geography?.direction?.toString().orEmpty(),
                    latitude = depFlight.geography?.latitude?.toString().orEmpty(),
                    longitude = depFlight.geography?.longitude?.toString().orEmpty(),
                    hSpeed = depFlight.speed?.horizontal?.toString().orEmpty(),
                    vSpeed = depFlight.speed?.vspeed?.toString().orEmpty(),
                    status = depFlight.status.orEmpty(),
                    squawk = depFlight.system?.squawk.orEmpty(),
                    modelName = airPlane?.productionLine.orEmpty(),
                    modelCode = airPlane?.modelCode.orEmpty(),
                    airCraftType = airPlane?.enginesType.orEmpty(),
                    regNo = airPlane?.numberRegistration.orEmpty(),
                    iataModel = airPlane?.airplaneIataType.orEmpty(),
                    icaoHex = airPlane?.hexIcaoAirplane.orEmpty(),
                    productionLine = airPlane?.productionLine.orEmpty(),
                    series = airPlane?.planeSeries.orEmpty(),
                    lineNumber = airPlane?.lineNumber.orEmpty(),
                    constructionNo = airPlane?.constructionNumber.orEmpty(),
                    firstFlight = airPlane?.firstFlight.orEmpty(),
                    deliveryDate = airPlane?.deliveryDate.orEmpty(),
                    rolloutDate = airPlane?.rolloutDate.orEmpty(),
                    currentOwner = airPlane?.planeOwner.orEmpty(),
                    planeStatus = airPlane?.planeStatus.orEmpty(),
                    airLineIataCode = airPlane?.codeIataAirline.orEmpty(),
                    airLineICaoCode = airPlane?.codeIcaoAirline.orEmpty(),
                )
            )
        }

        return departureFlightData
    }

    fun getDepartureFlightDataFromAirline(airLineDetail: StaticAirLineItems): ArrayList<ArrivalDataItems> {
        val departureFlightList: List<FlightDataItem> =
            liveFlight.filter { it.airline?.iataCode == airLineDetail.codeIataAirline }

        val departureFlightData = ArrayList<ArrivalDataItems>()
        departureFlightList.forEach { depFlight ->
            val arrAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == depFlight.arrival?.iataCode
            }
            val depAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == depFlight.departure?.iataCode
            }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight = scheduleFlightList.firstOrNull {
                it.airline?.iataCode == depFlight.airline?.iataCode
            }

            val airPlane: AirPlaneItems? = airPlane.firstOrNull {
                it.codeIataAirline == depFlight.airline?.iataCode
            }

            departureFlightData.add(
                ArrivalDataItems(
                    depFlight.flight?.iataNumber.orEmpty(),
                    depFlight.departure?.iataCode.orEmpty(),
                    depFlight.arrival?.iataCode.orEmpty(),
                    arrAirport?.nameAirport.orEmpty(),
                    depAirport?.nameAirport.orEmpty(),
                    arrCity?.nameCity.orEmpty(),
                    depCity?.nameCity.orEmpty(),
                    "",
                    depFlight.flight?.icaoNumber.orEmpty(),
                    scheduledArrTime = scheduleFlight?.arrival?.actualTime.orEmpty(),
                    scheduledDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                    estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime.orEmpty(),
                    flightIataNumber = depFlight.flight?.iataNumber.orEmpty(),
                    airlineName = scheduleFlight?.airline?.name.orEmpty(),
                    flightIcaoNo = depFlight.flight?.icaoNumber.orEmpty(),
                    terminal = scheduleFlight?.arrival?.terminal.orEmpty(),
                    gate = scheduleFlight?.departure?.gate.orEmpty(),
                    delay = scheduleFlight?.departure?.delay?.toString().orEmpty(),
                    scheduled = scheduleFlight?.departure?.scheduledTime.orEmpty(),
                    altitude = depFlight.geography?.altitude?.toString().orEmpty(),
                    direction = depFlight.geography?.direction?.toString().orEmpty(),
                    latitude = depFlight.geography?.latitude?.toString().orEmpty(),
                    longitude = depFlight.geography?.longitude?.toString().orEmpty(),
                    hSpeed = depFlight.speed?.horizontal?.toString().orEmpty(),
                    vSpeed = depFlight.speed?.vspeed?.toString().orEmpty(),
                    status = depFlight.status.orEmpty(),
                    squawk = depFlight.system?.squawk.orEmpty(),
                    modelName = airPlane?.productionLine.orEmpty(),
                    modelCode = airPlane?.modelCode.orEmpty(),
                    airCraftType = airPlane?.enginesType.orEmpty(),
                    regNo = airPlane?.numberRegistration.orEmpty(),
                    iataModel = airPlane?.airplaneIataType.orEmpty(),
                    icaoHex = airPlane?.hexIcaoAirplane.orEmpty(),
                    productionLine = airPlane?.productionLine.orEmpty(),
                    series = airPlane?.planeSeries.orEmpty(),
                    lineNumber = airPlane?.lineNumber.orEmpty(),
                    constructionNo = airPlane?.constructionNumber.orEmpty(),
                    firstFlight = airPlane?.firstFlight.orEmpty(),
                    deliveryDate = airPlane?.deliveryDate.orEmpty(),
                    rolloutDate = airPlane?.rolloutDate.orEmpty(),
                    currentOwner = airPlane?.planeOwner.orEmpty(),
                    planeStatus = airPlane?.planeStatus.orEmpty(),
                    airLineIataCode = airPlane?.codeIataAirline.orEmpty(),
                    airLineICaoCode = airPlane?.codeIcaoAirline.orEmpty(),
                )
            )
        }

        return departureFlightData
    }

    fun getDepartureFlightDataFromTailNumber(tailData: AirPlaneItems): ArrayList<ArrivalDataItems> {
        val departureFlightList: List<FlightDataItem> =
            liveFlight.filter { it.aircraft?.regNumber == tailData.numberRegistration }

        val departureFlightData = ArrayList<ArrivalDataItems>()
        departureFlightList.forEach { depFlight ->
            val arrAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == depFlight.arrival?.iataCode
            }
            val depAirport = airportsDataList.firstOrNull {
                it.codeIataAirport == depFlight.departure?.iataCode
            }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight =
                scheduleFlightList.firstOrNull { it.airline?.iataCode == depFlight.airline?.iataCode }

            val airPlane: AirPlaneItems? =
                airPlane.firstOrNull { it.codeIataAirline == depFlight.airline?.iataCode }

            departureFlightData.add(
                ArrivalDataItems(
                    depFlight.flight?.iataNumber.orEmpty(),
                    depFlight.departure?.iataCode.orEmpty(),
                    depFlight.arrival?.iataCode.orEmpty(),
                    arrAirport?.nameAirport.orEmpty(),
                    depAirport?.nameAirport.orEmpty(),
                    arrCity?.nameCity.orEmpty(),
                    depCity?.nameCity.orEmpty(),
                    "",
                    depFlight.flight?.icaoNumber.orEmpty(),
                    scheduleFlight?.arrival?.actualTime.orEmpty(),
                    scheduleFlight?.departure?.actualTime.orEmpty(),
                    scheduleFlight?.departure?.actualTime.orEmpty(),
                    scheduleFlight?.arrival?.estimatedTime.orEmpty(),
                    depFlight.flight?.iataNumber.orEmpty(),
                    scheduleFlight?.airline?.name.orEmpty(),
                    depFlight.flight?.icaoNumber.orEmpty(),
                    scheduleFlight?.arrival?.terminal.orEmpty(),
                    scheduleFlight?.departure?.gate.orEmpty(),
                    scheduleFlight?.departure?.delay?.toString().orEmpty(),
                    scheduleFlight?.departure?.scheduledTime.orEmpty(),
                    depFlight.geography?.altitude?.toString().orEmpty(),
                    depFlight.geography?.direction?.toString().orEmpty(),
                    depFlight.geography?.latitude?.toString().orEmpty(),
                    depFlight.geography?.longitude?.toString().orEmpty(),
                    depFlight.speed?.horizontal?.toString().orEmpty(),
                    depFlight.speed?.vspeed?.toString().orEmpty(),
                    depFlight.status.orEmpty(),
                    depFlight.system?.squawk.orEmpty(),
                    airPlane?.productionLine.orEmpty(),
                    airPlane?.modelCode.orEmpty(),
                    airPlane?.enginesType.orEmpty(),
                    airPlane?.numberRegistration.orEmpty(),
                    airPlane?.airplaneIataType.orEmpty(),
                    airPlane?.hexIcaoAirplane.orEmpty(),
                    airPlane?.productionLine.orEmpty(),
                    airPlane?.planeSeries.orEmpty(),
                    airPlane?.lineNumber.orEmpty(),
                    airPlane?.constructionNumber.orEmpty(),
                    airPlane?.firstFlight.orEmpty(),
                    airPlane?.deliveryDate.orEmpty(),
                    airPlane?.rolloutDate.orEmpty(),
                    airPlane?.planeOwner.orEmpty(),
                    airPlane?.planeStatus.orEmpty(),
                    airPlane?.codeIataAirline.orEmpty(),
                    airPlane?.codeIcaoAirline.orEmpty(),
                )
            )
        }

        return departureFlightData
    }

    fun getDepartureFlightDataFromAircraft(departureFlight: FlightDataItem): ArrayList<ArrivalDataItems> {
        val departureFlightData = ArrayList<ArrivalDataItems>()

        val arrAirport =
            airportsDataList.firstOrNull { it.codeIataAirport == departureFlight.arrival?.iataCode }
        val depAirport =
            airportsDataList.firstOrNull { it.codeIataAirport == departureFlight.departure?.iataCode }

        val codeIataCityDep = depAirport?.codeIataCity
        val codeIataCityArr = arrAirport?.codeIataCity

        val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
        val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

        val scheduleFlight = scheduleFlightList.firstOrNull {
            it.airline?.iataCode == departureFlight.airline?.iataCode
        }

        val airPlane = airPlane.firstOrNull {
            it.codeIataAirline == departureFlight.airline?.iataCode
        }

        val airPortDetail = airportsDataList.firstOrNull {
            it.codeIataAirport == departureFlight.arrival?.iataCode
        }

        departureFlightData.add(
            ArrivalDataItems(
                departureFlight.flight?.iataNumber ?: "",
                departureFlight.departure?.iataCode ?: "",
                departureFlight.arrival?.iataCode ?: "",
                arrAirport?.nameAirport ?: "",
                depAirport?.nameAirport ?: "",
                arrCity?.nameCity ?: "",
                depCity?.nameCity ?: "",
                airPortDetail?.nameAirport ?: "",
                departureFlight.flight?.icaoNumber ?: "",
                scheduledArrTime = scheduleFlight?.arrival?.actualTime ?: "",
                scheduledDepTime = scheduleFlight?.departure?.actualTime ?: "",
                actualDepTime = scheduleFlight?.departure?.actualTime ?: "",
                estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime ?: "",
                flightIataNumber = departureFlight.flight?.iataNumber ?: "",
                airlineName = scheduleFlight?.airline?.name ?: "",
                flightIcaoNo = departureFlight.flight?.icaoNumber ?: "",
                terminal = scheduleFlight?.arrival?.terminal ?: "",
                gate = scheduleFlight?.departure?.gate ?: "",
                delay = scheduleFlight?.departure?.delay?.toString() ?: "",
                scheduled = scheduleFlight?.departure?.scheduledTime ?: "",
                altitude = departureFlight.geography?.altitude?.toString() ?: "",
                direction = departureFlight.geography?.direction?.toString() ?: "",
                latitude = departureFlight.geography?.latitude?.toString() ?: "",
                longitude = departureFlight.geography?.longitude?.toString() ?: "",
                hSpeed = departureFlight.speed?.horizontal?.toString() ?: "",
                vSpeed = departureFlight.speed?.vspeed?.toString() ?: "",
                status = departureFlight.status ?: "",
                squawk = departureFlight.system?.squawk ?: "",
                modelName = airPlane?.productionLine ?: "",
                modelCode = airPlane?.modelCode ?: "",
                airCraftType = airPlane?.enginesType ?: "",
                regNo = airPlane?.numberRegistration ?: "",
                iataModel = airPlane?.airplaneIataType ?: "",
                icaoHex = airPlane?.hexIcaoAirplane ?: "",
                productionLine = airPlane?.productionLine ?: "",
                series = airPlane?.planeSeries ?: "",
                lineNumber = airPlane?.lineNumber ?: "",
                constructionNo = airPlane?.constructionNumber ?: "",
                firstFlight = airPlane?.firstFlight ?: "",
                deliveryDate = airPlane?.deliveryDate ?: "",
                rolloutDate = airPlane?.rolloutDate ?: "",
                currentOwner = airPlane?.planeOwner ?: "",
                planeStatus = airPlane?.planeStatus ?: "",
                airLineIataCode = airPlane?.codeIataAirline ?: "",
                airLineICaoCode = airPlane?.codeIcaoAirline ?: "",
            )
        )

        return departureFlightData
    }

    fun registeredBackPress() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        })
    }

    fun onBackPress() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null && view is EditText && imm.isAcceptingText) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.text?.clear()
            view.clearFocus()
            return
        }

        binding.apply {
            if (fragmentFlightDetails.root.y == statusBarHeight.toFloat()) {
                hideFragment(
                    fragmentFlightDetails
                )
            } else if (fragmentAirPortSearch.root.y == statusBarHeight.toFloat()) {
                hideFragment(
                    fragmentAirPortSearch
                )
            } else if (fragmentSearchAirCrafts.root.y == statusBarHeight.toFloat()) {
                hideFragment(
                    fragmentSearchAirCrafts
                )
            } else if (fragmentSearchAirLine.root.y == statusBarHeight.toFloat()) {
                hideFragment(
                    fragmentSearchAirLine
                )
            } else if (fragmentSearchTail.root.y == statusBarHeight.toFloat()) {
                hideFragment(
                    fragmentSearchTail
                )
            } else if (fragmentSearchAirport.root.y == statusBarHeight.toFloat()) {
                hideFragment(
                    fragmentSearchAirport
                )
            }
        }
    }

    private fun setUpAllFragments() {
        arrayOf(
            binding.fragmentSearchAirport.root as MyFragment<*>,
            binding.fragmentSearchAirCrafts.root as MyFragment<*>,
            binding.fragmentSearchAirLine.root as MyFragment<*>,
            binding.fragmentSearchTail.root as MyFragment<*>,
            binding.fragmentAirPortSearch.root as MyFragment<*>,
            binding.fragmentFlightDetails.root as MyFragment<*>,
            binding.fragmentTrackedFlight.root as MyFragment<*>
        ).forEach { fragment ->
            fragment.apply {
                setupFragment(this@MainActivity)
                y = mScreenHeight.toFloat()
                visible()
            }
        }
    }

    private fun initViewPagerAndBottomNavigationListener() {
        binding.apply {
            viewpager2.adapter = MainActivityViewPagerAdapter(this@MainActivity)
            viewpager2.isUserInputEnabled = false
            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_map -> {
                        window.statusBarColor =
                            ContextCompat.getColor(this@MainActivity, R.color.app_background_color)
                        viewpager2.currentItem = 0
                        closeAllOpenFragment()
                    }

                    R.id.nav_search -> {
                        window.statusBarColor =
                            ContextCompat.getColor(this@MainActivity, R.color.surface_color)
                        viewpager2.currentItem = 1
                    }

                    R.id.nav_nearby -> {
                        window.statusBarColor =
                            ContextCompat.getColor(this@MainActivity, R.color.surface_color)
                        viewpager2.currentItem = 2
                        closeAllOpenFragment()
                    }

                    R.id.nav_setting -> {
                        window.statusBarColor =
                            ContextCompat.getColor(this@MainActivity, R.color.surface_color)
                        viewpager2.currentItem = 3
                        closeAllOpenFragment()
                    }
                }
                true
            }
        }
    }

    private fun closeAllOpenFragment() {
        binding.apply {
            fragmentSearchAirport.root.y = mScreenHeight.toFloat()
            fragmentAirPortSearch.root.y = mScreenHeight.toFloat()
            fragmentFlightDetails.root.y = mScreenHeight.toFloat()
            fragmentSearchTail.root.y = mScreenHeight.toFloat()
            fragmentSearchAirLine.root.y = mScreenHeight.toFloat()
            fragmentSearchAirCrafts.root.y = mScreenHeight.toFloat()
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

                    BottomSheetBehavior.STATE_HIDDEN -> {}

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

    private var scheduleFlightList: List<SchedulesFlightsItems> = listOf()

    fun setData(
        flightData: FlightDataItem,
        depAirport: AirportsDataItems?,
        arvAirport: AirportsDataItems?,
        airLinesList: List<StaticAirLineItems>,
        citiesList: List<CitiesDataItems>,
        scheduleFlightList: List<SchedulesFlightsItems>,
        airPlanesList: List<AirPlaneItems>
    ) {
        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        binding.include.apply {
            AirCraftiataNumber.text = flightData.aircraft?.iataCode
            flightNum.text = flightData.flight?.iataNumber
            callSign.text = flightData.flight?.icaoNumber

            val airLine =
                airLinesList.filter { it.codeIataAirline == flightData.airline?.iataCode }[0]
            airlineName.text = airLine.nameAirline

            val airPlane =
                airPlanesList.filter { it.codeIataAirline == flightData.airline?.iataCode }[0]
            AirCarftName.text = airPlane.productionLine

            val altitudeFeet = flightData.geography?.altitude?.times(3.28084)
            AltitudeValue.text = if (altitudeFeet != null) {
                String.format(Locale.US, "%.7g ft", altitudeFeet)
            } else {
                "N/A"
            }

            val speedKmh = flightData.speed?.horizontal?.times(3.6)
            SpeedValue.text = if (speedKmh != null) {
                String.format(Locale.US, "%.7g km/h", speedKmh)
            } else {
                "N/A"
            }

            val deptIataCode = flightData.departure?.iataCode
            depIataCode.text = deptIataCode

            val arriIataCode = flightData.arrival?.iataCode
            arrivalIataCode.text = arriIataCode

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arvAirport?.codeIataCity

            val depCity = citiesList.filter { it.codeIataCity == codeIataCityDep }[0]
            val arrCity = citiesList.filter { it.codeIataCity == codeIataCityArr }[0]
            depCityName.text = depCity.nameCity
            arrCityName.text = arrCity.nameCity

            val scheduleFlight =
                scheduleFlightList.filter { it.airline?.iataCode == flightData.airline?.iataCode }[0]

            depTime.text = formatTo12HourTime(scheduleFlight.departure?.actualTime.toString())
            arriTime.text = formatTo12HourTime(scheduleFlight.arrival?.estimatedTime.toString())
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
    }

    private var trackedData: TrackedDataItem? = null
    private val trackingFlightDao: TrackedFlightDao by inject()

    fun setClickListener(searchTag: String) {
        binding.apply {
            when (searchTag) {
                "airport" -> {
                    visibleFragmentName = "airport"
                    showFragment(fragmentSearchAirport)
                }

                "aircraft" -> {
                    visibleFragmentName = "aircraft"
                    showFragment(fragmentSearchAirCrafts)
                }

                "airline" -> {
                    visibleFragmentName = "airline"
                    showFragment(fragmentSearchAirLine)
                }

                "tail" -> {
                    visibleFragmentName = "tail"
                    showFragment(fragmentSearchTail)
                }

                "flightDetails" -> {
                    visibleFragmentName = "flightDetails"
                    fragmentFlightDetails.root.setData()
                    showFragment(fragmentFlightDetails)
                }
            }
        }
    }

    private fun showFragment(fragment: ViewBinding, animationDuration: Long = 550L) {
        ObjectAnimator.ofFloat(fragment.root, "y", statusBarHeight.toFloat()).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun hideFragment(fragment: ViewBinding, animationDuration: Long = 550L) {
        ObjectAnimator.ofFloat(fragment.root, "y", mScreenHeight.toFloat()).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private var mTouchDownX = -1
    private var mTouchDownY = -1
    private var mMoveGestureThreshold = 0

    private fun isVerticalMovement(event: MotionEvent): Boolean {
        val dx = abs(mTouchDownX - event.x)
        val dy = abs(mTouchDownY - event.y)

        return dy > mMoveGestureThreshold && dy > dx
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        mDetector!!.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {}

            MotionEvent.ACTION_MOVE -> {
                val hasFingerMoved = if (mTouchDownX == -1 || mTouchDownY == -1) {
                    mTouchDownX = event.x.toInt()
                    mTouchDownY = event.y.toInt()
                    false
                } else {
                    isVerticalMovement(event)
                }

                val visibleBinding = getVisibleFragmentBinding()

                if (hasFingerMoved && isFragmentVisible(visibleBinding)) {
                    val diffY = mTouchDownY - event.y
                    val diffX = mTouchDownX - event.x

                    if (abs(diffY) > abs(diffX)) {
                        val newY = mAllAppsFragmentY - diffY
                        val clampedY = min(max(0f, newY), mScreenHeight.toFloat())

                        visibleBinding.root.y = clampedY
                    }
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                mTouchDownX = -1
                mTouchDownY = -1
                if (mIgnoreYMoveEvents) {
                    mIgnoreYMoveEvents = false
                } else {
                    val visibleBinding = getVisibleFragmentBinding()
                    if (isFragmentExpanded(visibleBinding)) hideFragment(visibleBinding)
                    else showFragment(visibleBinding)
                }
            }
        }
        return true
    }

    private fun isFragmentExpanded(fragment: ViewBinding) =
        fragment.root.y > mScreenHeight.toFloat() / 2

    private fun isFragmentVisible(visibleBinding: ViewBinding) =
        visibleBinding.root.y != mScreenHeight.toFloat()

    private fun getVisibleFragmentBinding(): ViewBinding {
        when (visibleFragmentName) {
            "airport" -> {
                return binding.fragmentSearchAirport
            }

            "aircraft" -> {
                return binding.fragmentSearchAirCrafts
            }

            "airline" -> {
                return binding.fragmentSearchAirLine
            }

            "tail" -> {
                return binding.fragmentSearchTail
            }

            "flightDetails" -> {
                return binding.fragmentFlightDetails
            }

            else -> return binding.fragmentSearchAirport
        }
    }

    private var mIgnoreYMoveEvents = false

    private var mAllAppsFragmentY = 0

    fun startHandlingTouches(touchDownY: Int) {
        mTouchDownY = touchDownY
    }

    override fun onFlingUp() {
        logDebug("dsada", "onFlingUp")
    }

    override fun onFlingDown() {
        logDebug("dsada", "onFlingDown")
    }

    override fun onFlingRight() {
        logDebug("dsada", "onFlingRight")
    }

    override fun onFlingLeft() {
        logDebug("dsada", "onFlingLeft")
    }
}