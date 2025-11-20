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
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.databinding.ActivitySearchAircraftBinding
import com.example.flighttrackerappnew.presentation.adapter.SearchAirCraftsAdapter
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_SEARCH_AIRCRAFT
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.getCurrentCountryLatLon
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isAirCraftApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isAirPortApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isCitiesApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFlightScheduleApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFlightTrackerApiSuccess
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.lat
import com.example.flighttrackerappnew.presentation.utils.lon
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

class SearchAircraftActivity :
    BaseActivity<ActivitySearchAircraftBinding>(ActivitySearchAircraftBinding::inflate) {
    private var citiesList = listOf<CitiesDataItems>()
    private var liveFlight: List<FlightDataItem>? = null
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var aircraftSearchAdapter: SearchAirCraftsAdapter? = null
    private val viewModel: FlightAppViewModel by inject()
    private var airportList = listOf<AirportsDataItems>()
    private var airCraft = listOf<AirPlaneItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isFromAirportOrAirline = false
        viewListener()
        observeData()
    }

    private fun observeData() {
        viewModel.apply {
            citiesData.observe(this@SearchAircraftActivity) { result ->
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
                        showDialog()
                    }
                }
            }
            liveFlightData.observe(this@SearchAircraftActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isFlightTrackerApiSuccess = false
                    }

                    is Resource.Success -> {
                        isFlightTrackerApiSuccess = true
                        liveFlight = result.data.filterNot { it.flight?.iataNumber == "XXD" }
                        setLayout()
                    }

                    is Resource.Error -> {
                        isFlightTrackerApiSuccess = false
                        showDialog()
                    }
                }
            }
            scheduleFlightData.observe(this@SearchAircraftActivity) { result ->
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
                        showDialog()
                    }
                }
            }
            airCraftData.observe(this@SearchAircraftActivity) { result ->
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
                        showDialog()
                    }
                }
            }
            airPortsData.observe(this@SearchAircraftActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        isAirPortApiSuccess = false
                        Log.d("MY----TAG", "observeLiveData:airPortsData Loading")
                    }

                    is Resource.Success -> {
                        airportList = result.data
                        isAirPortApiSuccess = true
                    }

                    is Resource.Error -> {
                        isAirPortApiSuccess = false
                        showToast("No Airport Data found")
                        Log.d("MY----TAG", "observeLiveData:No Airport Data found")
                        showDialog()
                    }
                }
            }
        }
    }

    private var dialog: Dialog? = null

    private fun showDialog() {
        if (dialog == null) {
            dialog =
                CustomDialogBuilder(this).setLayout(R.layout.dialog_retry_data).setCancelable(false)
                    .setPositiveClickListener {
                        it.dismiss()
                        dialog = null
                        if (!isAirPortApiSuccess) {
                            viewModel.getAirPorts()
                        }
                        if (!isCitiesApiSuccess) {
                            viewModel.getCities()
                        }
                        if (!isFlightScheduleApiSuccess) {
                            viewModel.getScheduleFlight()
                        }
                        if (!isAirCraftApiSuccess) {
                            viewModel.getAirCraft()
                        }
                        if (isAirPortApiSuccess) {
                            val pair = getCurrentCountryLatLon(this)
                            lat = pair?.first
                            lon = pair?.second
                            lat?.let { lat ->
                                lon?.let { lon ->
                                    viewModel.getLiveFlight(
                                        lat,
                                        lon,
                                        RemoteConfigManager.getString("distance").toInt()
                                    )
                                }
                            }
                        }
                    }.setNegativeClickListener {
                        it.dismiss()
                        dialog = null
                    }.show()
        }
    }

    private fun setLayout() {
        aircraftSearchAdapter = SearchAirCraftsAdapter()
        binding.recyclerView.adapter = aircraftSearchAdapter
        aircraftSearchAdapter?.let {
            if (liveFlight?.isNotEmpty() == true) {
                loadBannerAd()
            } else {
                binding.apply {
                    recyclerView.invisible()
                    ivSearchFlightSchedule.visible()
                    findHistory.visible()
                }
            }
            it.setList(liveFlight)
            it.setListener { liveFlight ->
                searchedDataTitle = ContextCompat.getString(
                    this@SearchAircraftActivity,
                    R.string.aircraft_track
                )
                startActivity(
                    Intent(
                        this@SearchAircraftActivity,
                        SearchedActivity::class.java
                    )
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.arrivalFlightData.postValue(
                        getArrivalFlightDataFromAircraft(
                            liveFlight
                        )
                    )
                    viewModel.departureFlightData.postValue(
                        getDepartureFlightDataFromAircraft(
                            liveFlight
                        )
                    )
                }
            }
        }
    }

    fun getArrivalFlightDataFromAircraft(liveFlight: FlightDataItem): ArrayList<FullDetailFlightData> {
        val arrivalFlightData = ArrayList<FullDetailFlightData>()

        val arrAirport = airportList.firstOrNull {
            it.codeIataAirport == liveFlight.arrival?.iataCode
        }

        val depAirport = airportList.firstOrNull {
            it.codeIataAirport == liveFlight.departure?.iataCode
        }

        val codeIataCityDep = depAirport?.codeIataCity
        val codeIataCityArr = arrAirport?.codeIataCity

        val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
        val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

        val scheduleFlight: FlightSchedulesItems? = scheduleFlightList.firstOrNull {
            it.airline?.iataCode == liveFlight.airline?.iataCode
        }

        val airPlane: AirPlaneItems? = airCraft.firstOrNull {
            it.codeIataAirline == liveFlight.airline?.iataCode
        }

        val airPortDetail = airportList.firstOrNull {
            it.codeIataAirport == liveFlight.arrival?.iataCode
        }

        arrivalFlightData.add(
            FullDetailFlightData(
                flightNo = liveFlight.flight?.iataNumber ?: "N/A",
                depIataCode = liveFlight.departure?.iataCode ?: "N/A",
                arrIataCode = liveFlight.arrival?.iataCode ?: "N/A",
                arrAirportName = arrAirport?.nameAirport ?: "N/A",
                depAirportName = depAirport?.nameAirport ?: "N/A",
                arrCity = arrCity?.nameCity ?: "N/A",
                depCity = depCity?.nameCity ?: "N/A",
                nameAirport = airPortDetail?.nameAirport ?: "N/A",
                callSign = liveFlight.flight?.icaoNumber ?: "N/A",
                scheduledArrTime = scheduleFlight?.arrival?.scheduledTime ?: "N/A",
                scheduledDepTime = scheduleFlight?.departure?.scheduledTime ?: "N/A",
                actualDepTime = scheduleFlight?.departure?.actualTime ?: "N/A",
                actualArrTime = scheduleFlight?.arrival?.actualTime.toString().orNA(),
                estimatedArrTime = scheduleFlight?.arrival?.estimatedTime.toString().orNA(),
                estimatedDepTime = scheduleFlight?.departure?.estimatedTime.toString().orNA(),
                flightIataNumber = liveFlight.flight?.iataNumber ?: "N/A",
                airlineName = scheduleFlight?.airline?.name ?: "N/A",
                flightIcaoNo = liveFlight.flight?.icaoNumber ?: "N/A",
                terminal = scheduleFlight?.arrival?.terminal ?: "N/A",
                gate = scheduleFlight?.arrival?.gate ?: "N/A",
                delay = scheduleFlight?.departure?.delay ?: "N/A",
                scheduled = scheduleFlight?.departure?.scheduledTime ?: "N/A",
                altitude = liveFlight.geography?.altitude.toString(),
                direction = liveFlight.geography?.direction.toString(),
                latitude = liveFlight.geography?.latitude.toString(),
                longitude = liveFlight.geography?.longitude.toString(),
                hSpeed = liveFlight.speed?.horizontal.toString(),
                vSpeed = liveFlight.speed?.vspeed.toString(),
                status = liveFlight.status ?: "N/A",
                squawk = liveFlight.system?.squawk ?: "N/A",
                modelName = airPlane?.productionLine ?: "N/A",
                modelCode = airPlane?.modelCode ?: "N/A",
                airCraftType = airPlane?.enginesType ?: "N/A",
                regNo = airPlane?.numberRegistration ?: "N/A",
                iataModel = airPlane?.airplaneIataType ?: "N/A",
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

        return arrivalFlightData
    }

    fun getDepartureFlightDataFromAircraft(liveFlight: FlightDataItem): ArrayList<FullDetailFlightData> {

        val departureFlightData = ArrayList<FullDetailFlightData>()

        val arrAirport =
            airportList.firstOrNull { it.codeIataAirport == liveFlight.arrival?.iataCode }
        val depAirport =
            airportList.firstOrNull { it.codeIataAirport == liveFlight.departure?.iataCode }

        val codeIataCityDep = depAirport?.codeIataCity
        val codeIataCityArr = arrAirport?.codeIataCity

        val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
        val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

        val scheduleFlight = scheduleFlightList.firstOrNull {
            it.airline?.iataCode == liveFlight.airline?.iataCode
        }

        val airPlane = airCraft.firstOrNull {
            it.codeIataAirline == liveFlight.airline?.iataCode
        }

        val airPortDetail = airportList.firstOrNull {
            it.codeIataAirport == liveFlight.arrival?.iataCode
        }

        departureFlightData.add(
            FullDetailFlightData(
                flightNo = liveFlight.flight?.iataNumber.orNA(),
                depIataCode = liveFlight.departure?.iataCode.orNA(),
                arrIataCode = liveFlight.arrival?.iataCode.orNA(),
                arrAirportName = arrAirport?.nameAirport.orNA(),
                depAirportName = depAirport?.nameAirport.orNA(),
                arrCity = arrCity?.nameCity.orNA(),
                depCity = depCity?.nameCity.orNA(),
                nameAirport = airPortDetail?.nameAirport.orNA(),
                callSign = liveFlight.flight?.icaoNumber.orNA(),
                scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orNA(),
                scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orNA(),
                actualDepTime = scheduleFlight?.departure?.actualTime.orNA(),
                actualArrTime = scheduleFlight?.arrival?.actualTime.toString().orNA(),
                estimatedArrTime = scheduleFlight?.arrival?.estimatedTime.orNA(),
                estimatedDepTime = scheduleFlight?.departure?.estimatedTime.toString().orNA(),
                flightIataNumber = liveFlight.flight?.iataNumber.orNA(),
                airlineName = scheduleFlight?.airline?.name.orNA(),
                flightIcaoNo = liveFlight.flight?.icaoNumber.orNA(),
                terminal = scheduleFlight?.departure?.terminal.orNA(),
                gate = scheduleFlight?.departure?.gate.orNA(),
                delay = scheduleFlight?.departure?.delay.toNAString(),
                scheduled = scheduleFlight?.departure?.scheduledTime.orNA(),
                altitude = liveFlight.geography?.altitude.toNAString(),
                direction = liveFlight.geography?.direction.toNAString(),
                latitude = liveFlight.geography?.latitude.toNAString(),
                longitude = liveFlight.geography?.longitude.toNAString(),
                hSpeed = liveFlight.speed?.horizontal.toNAString(),
                vSpeed = liveFlight.speed?.vspeed.toNAString(),
                status = liveFlight.status.orNA(),
                squawk = liveFlight.system?.squawk.orNA(),
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
        return departureFlightData
    }

    private fun loadBannerAd() {
        BANNER_SEARCH_AIRCRAFT.apply {
            loadAndShowBannerAd(
                context = this@SearchAircraftActivity,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {}
            )
        }
    }

    private fun viewListener() {
        window.manageKeyboardAndSystemUI(binding.root)
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            tvFlightNumber.addTextChangedListener(object : TextWatcher {
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
                    val filterList = liveFlight?.filter {
                        it.flight?.iataNumber?.lowercase()?.startsWith(text.lowercase()) == true
                    }

                    aircraftSearchAdapter?.setList(filterList)
                }
            })
        }
    }

}