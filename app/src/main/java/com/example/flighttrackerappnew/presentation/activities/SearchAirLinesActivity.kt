package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airLine.StaticAirLineItems
import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.arrival.ArrivalDataItems
import com.example.flighttrackerappnew.data.model.cities.CitiesDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.schedulesFlight.FlightSchedulesItems
import com.example.flighttrackerappnew.databinding.ActivitySearchAirLinesBinding
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adapter.SearchAirLinesAdapter
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.arrivalFlightData
import com.example.flighttrackerappnew.presentation.utils.departureFlightData
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.orNA
import com.example.flighttrackerappnew.presentation.utils.searchedDataTitle
import com.example.flighttrackerappnew.presentation.utils.toNAString
import com.example.flighttrackerappnew.presentation.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SearchAirLinesActivity :
    BaseActivity<ActivitySearchAirLinesBinding>(ActivitySearchAirLinesBinding::inflate) {

    private lateinit var liveFlight: List<FlightDataItem>
    private lateinit var airportsDataList: List<AirportsDataItems>
    private var citiesList = listOf<CitiesDataItems>()
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var airPlane = listOf<AirPlaneItems>()
    private var airLines = listOf<StaticAirLineItems>()
    private val dataCollector: DataCollector by inject()
    private var airlinesSearchAirLinesAdapter: SearchAirLinesAdapter? = null
    private val bannerAdManager: BannerAdManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        citiesList = dataCollector.cities
        airportsDataList = dataCollector.airports
        scheduleFlightList = dataCollector.schedules
        airPlane = dataCollector.planes
        liveFlight = dataCollector.flights
        airLines = dataCollector.staticAirlines
        setData()
        viewListener()

        isFromAirportOrAirline = true
    }

    private fun viewListener() {
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

    private fun setData() {
        if (airLines.isNotEmpty()) {
            val BANNER_SEARCH_AIRLINE =
                RemoteConfigManager.getBoolean("BANNER_SEARCH_AIRLINE")
            if (BANNER_SEARCH_AIRLINE && !config.isPremiumUser){
                binding.adContainerView.visible()
                bannerAdManager.loadAd(true, this, app.getString(R.string.BANNER_SEARCH_AIRLINE), {
                    bannerAdManager.showBannerAd(
                        binding.adContainerView,
                        this@SearchAirLinesActivity,
                        null
                    )
                }, {

                })
            }
        }else{
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
                arrivalFlightData.postValue(getArrivalFlightDataFromAirLine(airLineData))
                departureFlightData.postValue(getDepartureFlightDataFromAirline(airLineData))
            }
        }
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

            val arrAirportName = arrAirport?.nameAirport ?: "N/A"
            val depAirportName = depAirport?.nameAirport ?: "N/A"

            val depCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityDep }
            val arrCity = citiesList.firstOrNull { it.codeIataCity == codeIataCityArr }

            val scheduleFlight: FlightSchedulesItems? =
                scheduleFlightList.firstOrNull { it.airline?.iataCode == arrFlight.airline?.iataCode }

            val airPlane: AirPlaneItems? =
                airPlane.firstOrNull { it.codeIataAirline == arrFlight.airline?.iataCode }

            arrivalFlightData.add(
                ArrivalDataItems(
                    arrFlight.flight?.iataNumber.orNA(),
                    arrFlight.departure?.iataCode.orNA(),
                    arrFlight.arrival?.iataCode.orNA(),
                    arrAirportName.orNA(),
                    depAirportName.orNA(),
                    arrCity?.nameCity.orNA(),
                    depCity?.nameCity.orNA(),
                    "N/A",
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