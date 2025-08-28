package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.arrival.ArrivalDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.databinding.ActivitySearchAirportBinding
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adapter.SearchAirportAdapter
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.arrivalFlightData
import com.example.flighttrackerappnew.presentation.utils.departureFlightData
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.logDebug
import com.example.flighttrackerappnew.presentation.utils.orNA
import com.example.flighttrackerappnew.presentation.utils.searchedDataTitle
import com.example.flighttrackerappnew.presentation.utils.toNAString
import com.example.flighttrackerappnew.presentation.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SearchAirportActivity :
    BaseActivity<ActivitySearchAirportBinding>(ActivitySearchAirportBinding::inflate) {
    private val bannerAdManager: BannerAdManager by inject()

    var liveFlight = listOf<FlightDataItem>()
    private var airportsList = listOf<AirportsDataItems>()
    private var citiesList = listOf<CitiesDataItems>()
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var airPlane = listOf<AirPlaneItems>()
    private val dataCollector: DataCollector by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        citiesList = dataCollector.cities
        scheduleFlightList = dataCollector.schedules
        airPlane = dataCollector.planes
        liveFlight = dataCollector.flights
        airportsList = dataCollector.airports
        setAirPortData()
        viewListener()

        isFromAirportOrAirline = true
    }

    private fun viewListener() {
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
                    val adapter = binding.recyclerView.adapter as SearchAirportAdapter
                    val filterList: List<AirportsDataItems> = airportsList
                        .filter {
                            it.nameAirport?.lowercase()?.startsWith(text.lowercase()) == true
                        }

                    adapter.setList(filterList)
                }
            })
        }
    }

    private fun setAirPortData() {
        val airportIataCodes: Set<String> = buildSet {
            liveFlight.mapNotNullTo(this) { it.arrival?.iataCode?.lowercase() }
        }
        val matchedAirports = airportsList.filter { airport ->
            airport.codeIataAirport?.lowercase() in airportIataCodes
        }
        if (matchedAirports.isNotEmpty()) {
            val BANNER_SEARCH_AIRPORT =
                RemoteConfigManager.getBoolean("BANNER_SEARCH_AIRPORT")
            if (BANNER_SEARCH_AIRPORT && !config.isPremiumUser) {
                binding.adContainerView.visible()
                bannerAdManager.loadAd(true, this, app.getString(R.string.BANNER_SEARCH_AIRPORT), {
                    bannerAdManager.showBannerAd(
                        binding.adContainerView,
                        this@SearchAirportActivity,
                        null
                    )
                }, {

                })
            }
        } else {
            binding.recyclerView.invisible()
            binding.ivSearchFlightSchedule.visible()
            binding.findHistory.visible()
        }
        searchAirportAdapter = SearchAirportAdapter()
        binding.recyclerView.adapter = searchAirportAdapter
        searchAirportAdapter?.setList(matchedAirports)
        searchAirportAdapter?.setListener { airPortDetail ->
            searchedDataTitle =
                ContextCompat.getString(this@SearchAirportActivity, R.string.airport_search)
            startActivity(
                Intent(
                    this@SearchAirportActivity,
                    AirportSearchActivity::class.java
                )
            )
            lifecycleScope.launch(Dispatchers.IO) {
                arrivalFlightData.postValue(getArrivalFlightDataFromAirport(airPortDetail))
                departureFlightData.postValue(getDepartureFlightDataFromAirport(airPortDetail))
            }
        }
    }

    private var searchAirportAdapter: SearchAirportAdapter? = null

    fun getArrivalFlightDataFromAirport(airPortDetail: AirportsDataItems): ArrayList<ArrivalDataItems> {
        val arrivalFlightList = liveFlight.filter {
            it.arrival?.iataCode == airPortDetail.codeIataAirport
        }

        val arrivalFlightData = ArrayList<ArrivalDataItems>()

        arrivalFlightList.forEach { arrFlight ->
            val arrAirport = airportsList.firstOrNull {
                it.codeIataAirport == arrFlight.arrival?.iataCode
            }

            val depAirport = airportsList.firstOrNull {
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

            logDebug("ajdhaid", "${scheduleFlight?.airline?.name}")
            arrivalFlightData.add(
                ArrivalDataItems(
                    arrFlight.flight?.iataNumber.orNA(),
                    arrFlight.departure?.iataCode.orNA(),
                    arrFlight.arrival?.iataCode.orNA(),
                    arrAirport?.nameAirport.orNA(),
                    depAirport?.nameAirport.orNA(),
                    arrCity?.nameCity.orNA(),
                    depCity?.nameCity.orNA(),
                    airPortDetail.nameAirport.orNA(),
                    arrFlight.flight?.icaoNumber.orNA(),
                    scheduledArrTime = scheduleFlight?.arrival?.scheduledTime.orNA(),
                    scheduledDepTime = scheduleFlight?.departure?.scheduledTime.orNA(),
                    actualDepTime = scheduleFlight?.departure?.actualTime.orNA(),
                    estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime.orNA(),
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
                    regDate = airPlane?.registrationDate.orNA()
                )
            )
        }

        return arrivalFlightData
    }

    fun getDepartureFlightDataFromAirport(airPortDetail: AirportsDataItems): ArrayList<ArrivalDataItems> {
        val departureFlightList: List<FlightDataItem> =
            liveFlight.filter { it.departure?.iataCode == airPortDetail.codeIataCity }

        val departureFlightData = ArrayList<ArrivalDataItems>()
        departureFlightList.forEach { depFlight ->
            val arrAirport = airportsList.firstOrNull {
                it.codeIataAirport == depFlight.arrival?.iataCode
            }
            val depAirport = airportsList.firstOrNull {
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
                    depFlight.flight?.iataNumber ?: "N/A",
                    depFlight.departure?.iataCode ?: "N/A",
                    depFlight.arrival?.iataCode ?: "N/A",
                    arrAirport?.nameAirport ?: "N/A",
                    depAirport?.nameAirport ?: "N/A",
                    arrCity?.nameCity ?: "N/A",
                    depCity?.nameCity ?: "N/A",
                    airPortDetail.nameAirport ?: "N/A",
                    depFlight.flight?.icaoNumber ?: "N/A",
                    scheduledArrTime = scheduleFlight?.arrival?.actualTime ?: "N/A",
                    scheduledDepTime = scheduleFlight?.departure?.actualTime ?: "N/A",
                    actualDepTime = scheduleFlight?.departure?.actualTime ?: "N/A",
                    estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime ?: "N/A",
                    flightIataNumber = depFlight.flight?.iataNumber ?: "N/A",
                    airlineName = scheduleFlight?.airline?.name ?: "N/A",
                    flightIcaoNo = depFlight.flight?.icaoNumber ?: "N/A",
                    terminal = scheduleFlight?.arrival?.terminal ?: "N/A",
                    gate = scheduleFlight?.departure?.gate ?: "N/A",
                    delay = scheduleFlight?.departure?.delay?.toString() ?: "N/A",
                    scheduled = scheduleFlight?.departure?.scheduledTime ?: "N/A",
                    altitude = depFlight.geography?.altitude?.toString() ?: "N/A",
                    direction = depFlight.geography?.direction?.toString() ?: "N/A",
                    latitude = depFlight.geography?.latitude?.toString() ?: "N/A",
                    longitude = depFlight.geography?.longitude?.toString() ?: "N/A",
                    hSpeed = depFlight.speed?.horizontal?.toString() ?: "N/A",
                    vSpeed = depFlight.speed?.vspeed?.toString() ?: "N/A",
                    status = depFlight.status ?: "N/A",
                    squawk = depFlight.system?.squawk ?: "N/A",
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
                )
            )
        }

        return departureFlightData
    }

}