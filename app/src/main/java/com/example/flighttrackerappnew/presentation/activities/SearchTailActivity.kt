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
import com.example.flighttrackerappnew.databinding.ActivitySeacrhTailBinding
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adapter.SearchAirportAdapter
import com.example.flighttrackerappnew.presentation.adapter.SearchTailAdapter
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.arrivalFlightData
import com.example.flighttrackerappnew.presentation.utils.departureFlightData
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.orNA
import com.example.flighttrackerappnew.presentation.utils.searchedDataTitle
import com.example.flighttrackerappnew.presentation.utils.selectedLiveFlightData
import com.example.flighttrackerappnew.presentation.utils.toNAString
import com.example.flighttrackerappnew.presentation.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class SearchTailActivity :
    BaseActivity<ActivitySeacrhTailBinding>(ActivitySeacrhTailBinding::inflate) {

    private lateinit var liveFlight: List<FlightDataItem>
    private lateinit var airportsDataList: List<AirportsDataItems>
    private var citiesList = listOf<CitiesDataItems>()
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var airPlane = listOf<AirPlaneItems>()
    private val dataCollector: DataCollector by inject()
    private val bannerAdManager: BannerAdManager by inject()

    private var searchTailAdapter: SearchTailAdapter? = null

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
        setData()
        viewListener()

        isFromAirportOrAirline = false
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
                    val adapter = binding.recyclerView.adapter as SearchTailAdapter
                    val filterList = dataCollector.matchingAirplanes
                        .filter {
                            it.numberRegistration?.lowercase()?.startsWith(text.lowercase()) == true
                        }

                    adapter.setList(filterList)
                }
            })
        }

    }

    private fun setData() {
        searchTailAdapter = SearchTailAdapter()
        binding.recyclerView.adapter = searchTailAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            delay(1000)
            if (dataCollector.matchingAirplanes.isEmpty()) {
                binding.recyclerView.invisible()
                binding.ivSearchFlightSchedule.visible()
                binding.findHistory.visible()
                withContext(Dispatchers.Main) {
                    binding.pg.invisible()
                }
            } else {
                withContext(Dispatchers.Main) {
                    val BANNER_SEARCH_TAIL =
                        RemoteConfigManager.getBoolean("BANNER_SEARCH_TAIL")
                    if (BANNER_SEARCH_TAIL && !config.isPremiumUser) {
                        binding.adContainerView.visible()
                        bannerAdManager.loadAd(
                            true,
                            this@SearchTailActivity,
                            app.getString(R.string.BANNER_SEARCH_TAIL),
                            {
                                bannerAdManager.showBannerAd(
                                    binding.adContainerView,
                                    this@SearchTailActivity,
                                    null
                                )
                            },
                            {
                            })
                    }

                    searchTailAdapter?.setList(dataCollector.matchingAirplanes)
                    searchTailAdapter?.setListener { tailData: AirPlaneItems? ->
                        searchedDataTitle =
                            ContextCompat.getString(this@SearchTailActivity, R.string.tail_number)
                        startActivity(
                            Intent(
                                this@SearchTailActivity,
                                AirportSearchActivity::class.java
                            )
                        )
                        tailData?.let {
                            lifecycleScope.launch(Dispatchers.IO) {
                                arrivalFlightData.postValue(getArrivalFlightDataFromTailNumber(it))
                                departureFlightData.postValue(
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
                    arrFlight.flight?.iataNumber.orNA(),
                    arrFlight.departure?.iataCode.orNA(),
                    arrFlight.arrival?.iataCode.orNA(),
                    arrAirport?.nameAirport.orNA(),
                    depAirport?.nameAirport.orNA(),
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
                    depFlight.flight?.iataNumber ?: "N/A",
                    depFlight.departure?.iataCode ?: "N/A",
                    depFlight.arrival?.iataCode ?: "N/A",
                    arrAirport?.nameAirport ?: "N/A",
                    depAirport?.nameAirport ?: "N/A",
                    arrCity?.nameCity ?: "N/A",
                    depCity?.nameCity ?: "N/A",
                    "",
                    depFlight.flight?.icaoNumber ?: "N/A",
                    scheduleFlight?.arrival?.actualTime ?: "N/A",
                    scheduleFlight?.departure?.actualTime ?: "N/A",
                    scheduleFlight?.departure?.actualTime ?: "N/A",
                    scheduleFlight?.arrival?.estimatedTime ?: "N/A",
                    depFlight.flight?.iataNumber ?: "N/A",
                    scheduleFlight?.airline?.name ?: "N/A",
                    depFlight.flight?.icaoNumber ?: "N/A",
                    scheduleFlight?.arrival?.terminal ?: "N/A",
                    scheduleFlight?.departure?.gate ?: "N/A",
                    scheduleFlight?.departure?.delay?.toString() ?: "N/A",
                    scheduleFlight?.departure?.scheduledTime ?: "N/A",
                    depFlight.geography?.altitude?.toString() ?: "N/A",
                    depFlight.geography?.direction?.toString() ?: "N/A",
                    depFlight.geography?.latitude?.toString() ?: "N/A",
                    depFlight.geography?.longitude?.toString() ?: "N/A",
                    depFlight.speed?.horizontal?.toString() ?: "N/A",
                    depFlight.speed?.vspeed?.toString() ?: "N/A",
                    depFlight.status ?: "N/A",
                    depFlight.system?.squawk ?: "N/A",
                    airPlane?.productionLine ?: "N/A",
                    airPlane?.modelCode ?: "N/A",
                    airPlane?.enginesType ?: "N/A",
                    airPlane?.numberRegistration ?: "N/A",
                    airPlane?.airplaneIataType ?: "N/A",
                    airPlane?.hexIcaoAirplane ?: "N/A",
                    airPlane?.productionLine ?: "N/A",
                    airPlane?.planeSeries ?: "N/A",
                    airPlane?.lineNumber ?: "N/A",
                    airPlane?.constructionNumber ?: "N/A",
                    airPlane?.firstFlight ?: "N/A",
                    airPlane?.deliveryDate ?: "N/A",
                    airPlane?.rolloutDate ?: "N/A",
                    airPlane?.planeOwner ?: "N/A",
                    airPlane?.planeStatus ?: "N/A",
                    airPlane?.codeIataAirline ?: "N/A",
                    airPlane?.codeIcaoAirline ?: "N/A",
                    airPlaneIataCode = airPlane?.codeIataPlaneLong.toString(),
                    engineCount = airPlane?.enginesCount ?: "N/A",
                    regDate = airPlane?.registrationDate ?: "N/A",
                )
            )
        }

        return departureFlightData
    }
}