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
import com.example.flighttrackerappnew.databinding.ActivitySearchAircraftBinding
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.adapter.SearchAirCraftsAdapter
import com.example.flighttrackerappnew.presentation.getAllApsData.DataCollector
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.arrivalFlightData
import com.example.flighttrackerappnew.presentation.utils.departureFlightData
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.orNA
import com.example.flighttrackerappnew.presentation.utils.searchedDataTitle
import com.example.flighttrackerappnew.presentation.utils.toNAString
import com.example.flighttrackerappnew.presentation.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SearchAircraftActivity :
    BaseActivity<ActivitySearchAircraftBinding>(ActivitySearchAircraftBinding::inflate) {

    private lateinit var liveFlight: List<FlightDataItem>
    private lateinit var airportsDataList: List<AirportsDataItems>
    private var citiesList = listOf<CitiesDataItems>()
    private var scheduleFlightList = listOf<FlightSchedulesItems>()
    private var airPlane = listOf<AirPlaneItems>()
    private val dataCollector: DataCollector by inject()
    private var aircraftSearchAdapter: SearchAirCraftsAdapter? = null
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
        setData()
        viewListener()

        isFromAirportOrAirline = false
    }

    private fun viewListener() {
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
                    val adapter = binding.recyclerView.adapter as SearchAirCraftsAdapter
                    val filterList = liveFlight.filter {
                        it.flight?.iataNumber?.lowercase()?.startsWith(text.lowercase()) == true
                    }

                    adapter.setList(filterList)
                }
            })
        }
    }

    private fun setData() {
        aircraftSearchAdapter = SearchAirCraftsAdapter()
        binding.recyclerView.adapter = aircraftSearchAdapter
        aircraftSearchAdapter?.let {
            if (liveFlight.isNotEmpty()) {
                val BANNER_SEARCH_AIRCRAFT =
                    RemoteConfigManager.getBoolean("BANNER_SEARCH_AIRCRAFT")
                if (BANNER_SEARCH_AIRCRAFT) {
                    binding.adContainerView.visible()
                    bannerAdManager.loadAd(
                        true,
                        this,
                        app.getString(R.string.BANNER_SEARCH_AIRCRAFT),
                        {
                            bannerAdManager.showBannerAd(
                                binding.adContainerView,
                                this@SearchAircraftActivity,
                                null
                            )
                        },
                        {

                        })
                }
            }
            it.setList(liveFlight)
            it.setListener { arrivalFlight ->
                searchedDataTitle = ContextCompat.getString(
                    this@SearchAircraftActivity,
                    R.string.aircraft_track
                )
                startActivity(
                    Intent(
                        this@SearchAircraftActivity,
                        AirportSearchActivity::class.java
                    )
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    arrivalFlightData.postValue(getArrivalFlightDataFromAircraft(arrivalFlight))
                    departureFlightData.postValue(getDepartureFlightDataFromAircraft(arrivalFlight))

                }
            }
        }
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

        val scheduleFlight: FlightSchedulesItems? = scheduleFlightList.firstOrNull {
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
                flightNo = arrivalFlight.flight?.iataNumber ?: "N/A",
                depIataCode = arrivalFlight.departure?.iataCode ?: "N/A",
                arrIataCode = arrivalFlight.arrival?.iataCode ?: "N/A",
                arrAirportName = arrAirport?.nameAirport ?: "N/A",
                depAirportName = depAirport?.nameAirport ?: "N/A",
                arrCity = arrCity?.nameCity ?: "N/A",
                depCity = depCity?.nameCity ?: "N/A",
                nameAirport = airPortDetail?.nameAirport ?: "N/A",
                callSign = arrivalFlight.flight?.icaoNumber ?: "N/A",
                scheduledArrTime = scheduleFlight?.arrival?.scheduledTime ?: "N/A",
                scheduledDepTime = scheduleFlight?.departure?.scheduledTime ?: "N/A",
                actualDepTime = scheduleFlight?.departure?.actualTime ?: "N/A",
                estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime ?: "N/A",
                flightIataNumber = arrivalFlight.flight?.iataNumber ?: "N/A",
                airlineName = scheduleFlight?.airline?.name ?: "N/A",
                flightIcaoNo = arrivalFlight.flight?.icaoNumber ?: "N/A",
                terminal = scheduleFlight?.arrival?.terminal ?: "N/A",
                gate = scheduleFlight?.arrival?.gate ?: "N/A",
                delay = scheduleFlight?.departure?.delay ?: "N/A",
                scheduled = scheduleFlight?.departure?.scheduledTime ?: "N/A",
                altitude = arrivalFlight.geography?.altitude.toString(),
                direction = arrivalFlight.geography?.direction.toString(),
                latitude = arrivalFlight.geography?.latitude.toString(),
                longitude = arrivalFlight.geography?.longitude.toString(),
                hSpeed = arrivalFlight.speed?.horizontal.toString(),
                vSpeed = arrivalFlight.speed?.vspeed.toString(),
                status = arrivalFlight.status ?: "N/A",
                squawk = arrivalFlight.system?.squawk ?: "N/A",
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

        return arrivalFlightData
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
                departureFlight.flight?.iataNumber.orNA(),
                departureFlight.departure?.iataCode.orNA(),
                departureFlight.arrival?.iataCode.orNA(),
                arrAirport?.nameAirport.orNA(),
                depAirport?.nameAirport.orNA(),
                arrCity?.nameCity.orNA(),
                depCity?.nameCity.orNA(),
                airPortDetail?.nameAirport.orNA(),
                departureFlight.flight?.icaoNumber.orNA(),
                scheduledArrTime = scheduleFlight?.arrival?.actualTime.orNA(),
                scheduledDepTime = scheduleFlight?.departure?.actualTime.orNA(),
                actualDepTime = scheduleFlight?.departure?.actualTime.orNA(),
                estimatedArrivalTime = scheduleFlight?.arrival?.estimatedTime.orNA(),
                flightIataNumber = departureFlight.flight?.iataNumber.orNA(),
                airlineName = scheduleFlight?.airline?.name.orNA(),
                flightIcaoNo = departureFlight.flight?.icaoNumber.orNA(),
                terminal = scheduleFlight?.arrival?.terminal.orNA(),
                gate = scheduleFlight?.departure?.gate.orNA(),
                delay = scheduleFlight?.departure?.delay.toNAString(),
                scheduled = scheduleFlight?.departure?.scheduledTime.orNA(),
                altitude = departureFlight.geography?.altitude.toNAString(),
                direction = departureFlight.geography?.direction.toNAString(),
                latitude = departureFlight.geography?.latitude.toNAString(),
                longitude = departureFlight.geography?.longitude.toNAString(),
                hSpeed = departureFlight.speed?.horizontal.toNAString(),
                vSpeed = departureFlight.speed?.vspeed.toNAString(),
                status = departureFlight.status.orNA(),
                squawk = departureFlight.system?.squawk.orNA(),
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
                airPlaneIataCode = airPlane?.codeIcaoAirline.orNA(), // Looks like you might want codeIataPlaneLong here?
                engineCount = airPlane?.enginesCount.toNAString(),
                regDate = airPlane?.registrationDate.orNA()
            )
        )


        return departureFlightData
    }
}