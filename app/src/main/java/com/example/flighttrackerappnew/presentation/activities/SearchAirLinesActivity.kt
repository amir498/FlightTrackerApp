package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.databinding.ActivitySearchAirLinesBinding
import com.example.flighttrackerappnew.presentation.adapter.SearchAirLinesAdapter
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_SEARCH_AIRLINE
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isAirCraftApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isAirLineApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isAirPortApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isCitiesApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFlightScheduleApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFlightTrackerApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
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

class SearchAirLinesActivity :
    BaseActivity<ActivitySearchAirLinesBinding>(ActivitySearchAirLinesBinding::inflate) {

    private var citiesList = listOf<CitiesDataItems>()
    private lateinit var liveFlight: List<FlightDataItem>
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private val viewModel: FlightAppViewModel by inject()
    private var airportList = listOf<AirportsDataItems>()
    private var airCraft = listOf<AirPlaneItems>()
    private var airLines = listOf<StaticAirLineItems>()
    private var airlinesSearchAirLinesAdapter: SearchAirLinesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isFromAirportOrAirline = true
        viewListener()
        observeData()

    }

    private fun observeData() {
        viewModel.apply {
            staticAirLineData.observe(this@SearchAirLinesActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isAirLineApiSuccess = false
                    }

                    is Resource.Success -> {
                        isAirLineApiSuccess = true
                        airLines = result.data
                    }

                    is Resource.Error -> {
                        isAirLineApiSuccess = false
                    }
                }
            }
            citiesData.observe(this@SearchAirLinesActivity) { result ->
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
            liveFlightData.observe(this@SearchAirLinesActivity) { result ->
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
            scheduleFlightData.observe(this@SearchAirLinesActivity) { result ->
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
            airCraftData.observe(this@SearchAirLinesActivity) { result ->
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
            airPortsData.observe(this@SearchAirLinesActivity) { result ->
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

    private fun viewListener() {
        window.manageKeyboardAndSystemUI(binding.root)
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.tvAirLines.addTextChangedListener(object : TextWatcher {
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
                val adapter = binding.recyclerView.adapter as SearchAirLinesAdapter
                val filterList: List<StaticAirLineItems> = airLines.filter {
                    it.nameAirline?.lowercase()?.startsWith(text.lowercase()) == true
                }

                adapter.setList(filterList)
            }
        })
    }


    private fun setLayout() {
        if (airLines.isNotEmpty()) {
            loadBannerAd()
        } else {
            binding.recyclerView.invisible()
            binding.ivSearchFlightSchedule.visible()
            binding.findHistory.visible()
        }
        airlinesSearchAirLinesAdapter = SearchAirLinesAdapter()
        binding.recyclerView.adapter = airlinesSearchAirLinesAdapter
        airlinesSearchAirLinesAdapter?.setList(airLines)
        airlinesSearchAirLinesAdapter?.setListener { airLineData ->
            searchedDataTitle =
                ContextCompat.getString(this@SearchAirLinesActivity, R.string.airline_track)
            startActivity(
                Intent(
                    this@SearchAirLinesActivity,
                    AirportSearchActivity::class.java
                )
            )
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.arrivalFlightData.postValue(getArrivalFlightDataFromAirLine(airLineData))
                viewModel.departureFlightData.postValue(
                    getDepartureFlightDataFromAirline(
                        airLineData
                    )
                )
            }
        }
    }

    private fun loadBannerAd() {
        BANNER_SEARCH_AIRLINE.apply {
            loadAndShowBannerAd(
                context = this@SearchAirLinesActivity,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {}
            )
        }
    }

    fun getArrivalFlightDataFromAirLine(airLineDetail: StaticAirLineItems): ArrayList<FullDetailFlightData> {
        val arrivalFlightList: List<FlightDataItem> = liveFlight.filter {
            it.airline?.iataCode == airLineDetail.codeIataAirline && it.status == "en-route"
        }

        val arrivalFlightData = ArrayList<FullDetailFlightData>()
        arrivalFlightList.forEach { arrFlight ->
            val arrAirport =
                airportList.firstOrNull { it.codeIataAirport == arrFlight.arrival?.iataCode }
            val depAirport =
                airportList.firstOrNull { it.codeIataAirport == arrFlight.departure?.iataCode }

            val codeIataCityDep = depAirport?.codeIataCity
            val codeIataCityArr = arrAirport?.codeIataCity

            val arrAirportName = arrAirport?.nameAirport ?: "N/A"
            val depAirportName = depAirport?.nameAirport ?: "N/A"

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight: FlightSchedulesItems? =
                scheduleFlightList.firstOrNull { it.airline?.iataCode == arrFlight.airline?.iataCode }

            val airPlane: AirPlaneItems? =
                airCraft.firstOrNull { it.codeIataAirline == arrFlight.airline?.iataCode }

            arrivalFlightData.add(
                FullDetailFlightData(
                    flightNo = arrFlight.flight?.iataNumber.orNA(),
                    depIataCode = arrFlight.departure?.iataCode.orNA(),
                    arrIataCode = arrFlight.arrival?.iataCode.orNA(),
                    arrAirportName = arrAirportName.orNA(),
                    depAirportName = depAirportName.orNA(),
                    arrCity = arrCity?.nameCity.orNA(),
                    depCity = depCity?.nameCity.orNA(),
                    nameAirport = "N/A",
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

    fun getDepartureFlightDataFromAirline(airLineDetail: StaticAirLineItems): ArrayList<FullDetailFlightData> {
        val departureFlightList: List<FlightDataItem> =
            liveFlight.filter { it.airline?.iataCode == airLineDetail.codeIataAirline }

        val departureFlightData = ArrayList<FullDetailFlightData>()
        departureFlightList.forEach { depFlight ->
            val arrAirport = airportList.firstOrNull {
                it.codeIataAirport == depFlight.arrival?.iataCode
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

            val airPlane: AirPlaneItems? = airCraft.firstOrNull {
                it.codeIataAirline == depFlight.airline?.iataCode
            }

            departureFlightData.add(
                FullDetailFlightData(
                    flightNo = depFlight.flight?.iataNumber.orEmpty(),
                    depIataCode = depFlight.departure?.iataCode.orEmpty(),
                    arrIataCode = depFlight.arrival?.iataCode.orEmpty(),
                    arrAirportName = arrAirport?.nameAirport.orEmpty(),
                    depAirportName = depAirport?.nameAirport.orEmpty(),
                    arrCity = arrCity?.nameCity.orEmpty(),
                    depCity = depCity?.nameCity.orEmpty(),
                    nameAirport = "",
                    callSign = depFlight.flight?.icaoNumber.orEmpty(),
                    scheduledArrTime = scheduleFlight?.arrival?.actualTime.orEmpty(),
                    scheduledDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orEmpty(),
                    estimatedArrTime = scheduleFlight?.arrival?.estimatedTime.orEmpty(),
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
                    icaoHex = airPlane?.hexIcaoAirplane ?: "N/A",
                    productionLine = airPlane?.productionLine ?: "N/A",
                    series = airPlane?.planeSeries ?: "N/A",
                    lineNumber = airPlane?.lineNumber ?: "N/A",
                    constructionNo = airPlane?.constructionNumber ?: "N/A",
                    firstFlight = airPlane?.firstFlight ?: "N/A",
                    deliveryDate = airPlane?.deliveryDate ?: "N/A",
                    rolloutDate = airPlane?.rolloutDate ?: "N/A",
                    currentOwner = airPlane?.planeOwner ?: "N/A",
                    planeStatus = airPlane?.planeStatus ?: "N/A",
                    airLineIataCode = airPlane?.codeIataAirline ?: "N/A",
                    airLineICaoCode = airPlane?.codeIcaoAirline ?: "N/A",
                    airPlaneIataCode = airPlane?.codeIataPlaneLong ?: "N/A",
                    engineCount = airPlane?.enginesCount ?: "N/A",
                    regDate = airPlane?.registrationDate ?: "N/A",
                    progress = 0
                )
            )
        }

        return departureFlightData
    }
}