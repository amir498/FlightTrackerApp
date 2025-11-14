package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.databinding.ActivitySearchAirportBinding
import com.example.flighttrackerappnew.presentation.adapter.SearchAirportAdapter
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_SEARCH_AIRPORT
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isAirCraftApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isAirPortApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isCitiesApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFlightScheduleApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFlightTrackerApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.logDebug
import com.example.flighttrackerappnew.presentation.utils.manageKeyboardAndSystemUI
import com.example.flighttrackerappnew.presentation.utils.orNA
import com.example.flighttrackerappnew.presentation.utils.searchedDataTitle
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.toNAString
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.collections.contains

class SearchAirportActivity :
    BaseActivity<ActivitySearchAirportBinding>(ActivitySearchAirportBinding::inflate) {

    private val viewModel: FlightAppViewModel by inject()
    private var searchAirportAdapter = SearchAirportAdapter()
    private var airportList = listOf<AirportsDataItems>()
    var liveFlight = listOf<FlightDataItem>()
    private var citiesList = listOf<CitiesDataItems>()
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var airCraft = listOf<AirPlaneItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeData()
        viewListener()
        isFromAirportOrAirline = true
    }

    private fun viewListener() {
        window.manageKeyboardAndSystemUI(binding.root)
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            tvAirports.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    val filterList: List<AirportsDataItems> = airportList
                        .filter {
                            it.nameAirport?.lowercase()?.startsWith(text.lowercase()) == true
                        }
                    searchAirportAdapter.setList(filterList)
                }
            })
        }
    }

    private fun observeData() {
        viewModel.apply {
            citiesData.observe(this@SearchAirportActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isCitiesApiSuccess = false
                    }

                    is Resource.Success -> {
                        isCitiesApiSuccess = true
                        citiesList = result.data
                    }

                    is Resource.Error -> {
                        isCitiesApiSuccess = false
                    }
                }
            }
            liveFlightData.observe(this@SearchAirportActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isFlightTrackerApiSuccess = false
                    }

                    is Resource.Success -> {
                        isFlightTrackerApiSuccess = true
                        liveFlight = result.data
                    }

                    is Resource.Error -> {
                        isFlightTrackerApiSuccess = false
                    }
                }
            }
            scheduleFlightData.observe(this@SearchAirportActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isFlightScheduleApiSuccess = false
                    }

                    is Resource.Success -> {
                        isFlightScheduleApiSuccess = true
                        scheduleFlightList = result.data
                    }

                    is Resource.Error -> {
                        isFlightScheduleApiSuccess = false
                    }
                }
            }
            airCraftData.observe(this@SearchAirportActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isAirCraftApiSuccess = false
                    }

                    is Resource.Success -> {
                        isAirCraftApiSuccess = true
                        airCraft = result.data
                    }

                    is Resource.Error -> {
                        isAirCraftApiSuccess = false
                    }
                }
            }
            airPortsData.observe(this@SearchAirportActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isAirPortApiSuccess = false
                        Log.d("MY----TAG", "observeLiveData:airPortsData Loading")
                    }

                    is Resource.Success -> {
                        airportList = result.data
                        isAirPortApiSuccess = true
                        setLayout()
                    }

                    is Resource.Error -> {
                        isAirPortApiSuccess = false
                        showToast("No Airport Data found")
                        Log.d("MY----TAG", "observeLiveData:No Airport Data found")
                    }
                }
            }
        }
    }

    private fun setLayout() {
        val airportIataCodes: Set<String> = buildSet {
            liveFlight.mapNotNullTo(this) { it.arrival?.iataCode?.lowercase() }
        }
        val matchedAirports = airportList.filter { airport ->
            airport.codeIataAirport?.lowercase() in airportIataCodes
        }

        if (matchedAirports.isNotEmpty()) {
            loadBannerAd()
        } else {
            binding.recyclerView.invisible()
            binding.ivSearchFlightSchedule.visible()
            binding.findHistory.visible()
        }

        binding.recyclerView.adapter = searchAirportAdapter
        searchAirportAdapter.setList(matchedAirports)
        searchAirportAdapter.setListener { airPortDetail ->
            searchedDataTitle = ContextCompat.getString(this@SearchAirportActivity, R.string.airport_search)
            startActivity(Intent(
                this@SearchAirportActivity,
                AirportSearchActivity::class.java
            ))

            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.arrivalFlightData.postValue(getArrivalFlightDataFromAirport(airPortDetail))
                logDebug("asjdnan",getArrivalFlightDataFromAirport(airPortDetail).size.toString())
                viewModel.departureFlightData.postValue(
                    getDepartureFlightDataFromAirport(
                        airPortDetail
                    )
                )
            }
        }
    }

    fun getDepartureFlightDataFromAirport(airPortDetail: AirportsDataItems): ArrayList<FullDetailFlightData> {
        val departureFlightList = liveFlight.filter {
            it.departure?.iataCode == airPortDetail.codeIataAirport
        }

        val depFlightData = ArrayList<FullDetailFlightData>()

        departureFlightList.forEach { depFlight ->
            val arrAirport = airportList.firstOrNull {
                it.codeIataAirport == depFlight.departure?.iataCode
            }

            val depAirport = airportList.firstOrNull {
                it.codeIataAirport == depFlight.departure?.iataCode
            }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight = scheduleFlightList.firstOrNull {
                it.airline?.iataCode == depFlight.airline?.iataCode
            }

            val airPlane = airCraft.firstOrNull {
                it.codeIataAirline == depFlight.airline?.iataCode
            }

            depFlightData.add(
                FullDetailFlightData(
                    flightNo = depFlight.flight?.iataNumber.orNA(),
                    depIataCode = depFlight.departure?.iataCode.orNA(),
                    arrIataCode = depFlight.arrival?.iataCode.orNA(),
                    arrAirportName = arrAirport?.nameAirport.orNA(),
                    depAirportName = depAirport?.nameAirport.orNA(),
                    arrCity = arrCity?.nameCity.orNA(),
                    depCity = depCity?.nameCity.orNA(),
                    nameAirport = airPortDetail.nameAirport.orNA(),
                    callSign = depFlight.flight?.icaoNumber.orNA(),
                    scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orNA(),
                    scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orNA(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orNA(),
                    estimatedArrTime = scheduleFlight?.arrival?.estimatedTime.orNA(),
                    flightIataNumber = depFlight.flight?.iataNumber.orNA(),
                    airlineName = scheduleFlight?.airline?.name.orNA(),
                    flightIcaoNo = depFlight.flight?.icaoNumber.orNA(),
                    terminal = scheduleFlight?.departure?.terminal.orNA(),
                    gate = scheduleFlight?.departure?.gate.orNA(),
                    delay = scheduleFlight?.departure?.delay.toNAString(),
                    scheduled = scheduleFlight?.departure?.scheduledTime.orNA(),
                    altitude = depFlight.geography?.altitude.toNAString(),
                    direction = depFlight.geography?.direction.toNAString(),
                    latitude = depFlight.geography?.latitude.toNAString(),
                    longitude = depFlight.geography?.longitude.toNAString(),
                    hSpeed = depFlight.speed?.horizontal.toNAString(),
                    vSpeed = depFlight.speed?.vspeed.toNAString(),
                    status = depFlight.status.orNA(),
                    squawk = depFlight.system?.squawk.orNA(),
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
                    engineCount = airPlane?.enginesCount.toNAString(),
                    regDate = airPlane?.registrationDate.orNA(),
                    progress = 0
                )
            )
        }

        return depFlightData
    }

    fun getArrivalFlightDataFromAirport(airPortDetail: AirportsDataItems): ArrayList<FullDetailFlightData> {
        logDebug("asjdnan",airPortDetail.toString())
        logDebug("asjdnan",liveFlight.size.toString())
        logDebug("asjdnan",airportList.size.toString())
        logDebug("asjdnan",citiesList.size.toString())
        logDebug("asjdnan",airCraft.size.toString())
        logDebug("asjdnan",scheduleFlightList.size.toString())
        val arrivalFlightList = liveFlight.filter {
            it.arrival?.iataCode == airPortDetail.codeIataAirport
        }

        val arrivalFlightData = ArrayList<FullDetailFlightData>()

        arrivalFlightList.forEach { arrFlight ->
            val arrAirport = airportList.firstOrNull {
                it.codeIataAirport == arrFlight.arrival?.iataCode
            }

            val depAirport = airportList.firstOrNull {
                it.codeIataAirport == arrFlight.departure?.iataCode
            }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight = scheduleFlightList.firstOrNull {
                it.airline?.iataCode == arrFlight.airline?.iataCode
            }

            val airPlane = airCraft.firstOrNull {
                it.codeIataAirline == arrFlight.airline?.iataCode
            }

            arrivalFlightData.add(
                FullDetailFlightData(
                    flightNo = arrFlight.flight?.iataNumber.orNA(),
                    depIataCode = arrFlight.departure?.iataCode.orNA(),
                    arrIataCode = arrFlight.arrival?.iataCode.orNA(),
                    arrAirportName = arrAirport?.nameAirport.orNA(),
                    depAirportName = depAirport?.nameAirport.orNA(),
                    arrCity = arrCity?.nameCity.orNA(),
                    depCity = depCity?.nameCity.orNA(),
                    nameAirport = airPortDetail.nameAirport.orNA(),
                    callSign = arrFlight.flight?.icaoNumber.orNA(),
                    scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orNA(),
                    scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orNA(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orNA(),
                    estimatedArrTime = scheduleFlight?.arrival?.estimatedTime.orNA(),
                    flightIataNumber = arrFlight.flight?.iataNumber.orNA(),
                    airlineName = scheduleFlight?.airline?.name.orNA(),
                    flightIcaoNo = arrFlight.flight?.icaoNumber.orNA(),
                    terminal = scheduleFlight?.arrival?.terminal.orNA(),
                    gate = scheduleFlight?.arrival?.gate.orNA(),
                    delay = scheduleFlight?.departure?.delay.toNAString(),
                    scheduled = scheduleFlight?.departure?.scheduledTime.orNA(),
                    altitude = arrFlight.geography?.altitude.toNAString(),
                    direction = arrFlight.geography?.direction.toNAString(),
                    latitude = arrFlight.geography?.latitude.toNAString(),
                    longitude = arrFlight.geography?.longitude.toNAString(),
                    hSpeed = arrFlight.speed?.horizontal.toNAString(),
                    vSpeed = arrFlight.speed?.vspeed.toNAString(),
                    status = arrFlight.status.orNA(),
                    squawk = arrFlight.system?.squawk.orNA(),
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
                    engineCount = airPlane?.enginesCount.toNAString(),
                    regDate = airPlane?.registrationDate.orNA(),
                    progress = 0
                )
            )
        }

        return arrivalFlightData
    }

    private fun loadBannerAd() {
        BANNER_SEARCH_AIRPORT.apply {
            loadAndShowBannerAd(
                context = this@SearchAirportActivity,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {}
            )
        }
    }
}