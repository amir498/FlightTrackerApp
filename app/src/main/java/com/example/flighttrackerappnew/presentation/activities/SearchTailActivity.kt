package com.example.flighttrackerappnew.presentation.activities

import android.app.Dialog
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
import com.example.flighttrackerappnew.databinding.ActivitySeacrhTailBinding
import com.example.flighttrackerappnew.presentation.adapter.SearchTailAdapter
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_SEARCH_TAIL
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
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
import com.example.flighttrackerappnew.presentation.utils.matchingAirplanes
import com.example.flighttrackerappnew.presentation.utils.orNA
import com.example.flighttrackerappnew.presentation.utils.searchedDataTitle
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.toNAString
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class SearchTailActivity :
    BaseActivity<ActivitySeacrhTailBinding>(ActivitySeacrhTailBinding::inflate) {

    private var citiesList = listOf<CitiesDataItems>()
    private var liveFlight: List<FlightDataItem>? = null
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private val viewModel: FlightAppViewModel by inject()
    private var airportList = listOf<AirportsDataItems>()
    private var airCraft = listOf<AirPlaneItems>()
    private var airLines = listOf<StaticAirLineItems>()
    private var searchTailAdapter: SearchTailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewListener()
        observeData()
        isFromAirportOrAirline = false
    }

    private fun observeData() {
        viewModel.apply {
            staticAirLineData.observe(this@SearchTailActivity) { result ->
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
            citiesData.observe(this@SearchTailActivity) { result ->
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
            liveFlightData.observe(this@SearchTailActivity) { result ->
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
            scheduleFlightData.observe(this@SearchTailActivity) { result ->
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
            airCraftData.observe(this@SearchTailActivity) { result ->
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
            airPortsData.observe(this@SearchTailActivity) { result ->
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
                        binding.conPlaceHolder.visible()
                        binding.recyclerView.invisible()
                        showDialog()
                        isAirPortApiSuccess = false
                        showToast("No Airport Data found")
                        Log.d("MY----TAG", "observeLiveData:No Airport Data found")
                    }
                }
            }
        }
    }

    private var dialog: Dialog? = null

    private fun showLoading() {
        binding.pg.visible()
    }

    private fun showDialog() {
        if (dialog == null) {
            dialog = CustomDialogBuilder(this).setLayout(R.layout.dialog_retry).setCancelable(true)
                .setPositiveClickListener {
                    showLoading()
                    it.dismiss()
                    viewModel.getAirPorts()
                    dialog = null
                }.setNegativeClickListener {
                    it.dismiss()
                    dialog = null
                }.show()
        }
    }

    private fun loadBannerAd() {
        BANNER_SEARCH_TAIL.apply {
            loadAndShowBannerAd(
                context = this@SearchTailActivity,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {}
            )
        }
    }

    private fun setLayout() {
        searchTailAdapter = SearchTailAdapter()
        binding.recyclerView.adapter = searchTailAdapter

        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000)
            if (matchingAirplanes.isEmpty()) {
                withContext(Dispatchers.Main) {
                    binding.conPlaceHolder.visible()
                    binding.recyclerView.invisible()
                    binding.pg.invisible()
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.conPlaceHolder.invisible()
                    binding.recyclerView.visible()
                    loadBannerAd()
                    searchTailAdapter?.setList(matchingAirplanes)
                    searchTailAdapter?.setListener { tailData: AirPlaneItems? ->
                        searchedDataTitle =
                            ContextCompat.getString(this@SearchTailActivity, R.string.tail_number)
                        startActivity(
                            Intent(
                                this@SearchTailActivity, SearchedActivity::class.java
                            )
                        )
                        tailData?.let {
                            lifecycleScope.launch(Dispatchers.IO) {
                                viewModel.arrivalFlightData.postValue(
                                    getArrivalFlightDataFromTailNumber(it)
                                )
                                viewModel.departureFlightData.postValue(
                                    getDepartureFlightDataFromTailNumber(
                                        it
                                    )
                                )
                            }
                        }
                    }
                    binding.pg.invisible()
                }
            }
        }
    }

    fun getArrivalFlightDataFromTailNumber(tailData: AirPlaneItems): ArrayList<FullDetailFlightData> {

        val arrivalFlightList = liveFlight?.filter {
            it.aircraft?.regNumber == tailData.numberRegistration
        }

        val arrivalFlightData = ArrayList<FullDetailFlightData>()

        arrivalFlightList?.forEach { arrFlight ->
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
                    nameAirport = "N/A",
                    callSign = arrFlight.flight?.icaoNumber.orNA(),
                    scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orNA(),
                    scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orNA(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orNA(),
                    actualArrTime = scheduleFlight?.arrival?.actualTime.toString().orNA(),
                    estimatedArrTime = scheduleFlight?.arrival?.estimatedTime.toString().orNA(),
                    estimatedDepTime = scheduleFlight?.departure?.estimatedTime.toString().orNA(),
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
                    regDate = airPlane?.registrationDate.orNA().toString(),
                    progress = 0
                )
            )
        }

        return arrivalFlightData
    }

    fun getDepartureFlightDataFromTailNumber(tailData: AirPlaneItems): ArrayList<FullDetailFlightData> {
        val departureFlightList: List<FlightDataItem>? =
            liveFlight?.filter { it.aircraft?.regNumber == tailData.numberRegistration }

        val departureFlightData = ArrayList<FullDetailFlightData>()
        departureFlightList?.forEach { depFlight ->

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

            val scheduleFlight =
                scheduleFlightList.firstOrNull { it.airline?.iataCode == depFlight.airline?.iataCode }

            val airPlane: AirPlaneItems? =
                airCraft.firstOrNull { it.codeIataAirline == depFlight.airline?.iataCode }

            departureFlightData.add(
                FullDetailFlightData(
                    flightNo = depFlight.flight?.iataNumber.orNA(),
                    depIataCode = depFlight.departure?.iataCode.orNA(),
                    arrIataCode = depFlight.arrival?.iataCode.orNA(),
                    arrAirportName = arrAirport?.nameAirport.orNA(),
                    depAirportName = depAirport?.nameAirport.orNA(),
                    arrCity = arrCity?.nameCity.orNA(),
                    depCity = depCity?.nameCity.orNA(),
                    nameAirport = "N/A",
                    callSign = depFlight.flight?.icaoNumber.orNA(),
                    scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orNA(),
                    scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orNA(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orNA(),
                    actualArrTime = scheduleFlight?.arrival?.actualTime.toString().orNA(),
                    estimatedArrTime = scheduleFlight?.arrival?.estimatedTime.toString().orNA(),
                    estimatedDepTime = scheduleFlight?.departure?.estimatedTime.toString().orNA(),
                    flightIataNumber = depFlight.flight?.iataNumber.orNA(),
                    airlineName = scheduleFlight?.airline?.name.orNA(),
                    flightIcaoNo = depFlight.flight?.icaoNumber.orNA(),
                    terminal = scheduleFlight?.arrival?.terminal.orNA(),
                    gate = scheduleFlight?.arrival?.gate.orNA(),
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

        return departureFlightData
    }

    private fun viewListener() {
        window.manageKeyboardAndSystemUI(binding.root)
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
                    val filterList = matchingAirplanes.filter {
                        it.numberRegistration?.lowercase()?.startsWith(text.lowercase()) == true
                    }

                    searchTailAdapter?.setList(filterList)
                }
            })
        }
    }
}