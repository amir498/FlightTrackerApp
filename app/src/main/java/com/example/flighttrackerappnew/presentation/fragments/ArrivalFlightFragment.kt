package com.example.flighttrackerappnew.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.arrival.ArrivalDataItems
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.databinding.FragmentArrivalFlightBinding
import com.example.flighttrackerappnew.presentation.activities.AirportSearchActivity
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.DetailActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.adapter.ArrivalFlightAdapter
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.arrivalFlightData
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.searchedDataSubTitle
import com.example.flighttrackerappnew.presentation.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ArrivalFlightFragment : Fragment() {
    private val binding: FragmentArrivalFlightBinding by lazy {
        FragmentArrivalFlightBinding.inflate(layoutInflater)
    }

    private var adapter: ArrivalFlightAdapter? = null
    private var arrivalData = ArrayList<ArrivalDataItems>()
    private val nativeAdController: NativeAdController by inject()
    private val rewardedAd: RewardedAdManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        adapter = ArrivalFlightAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        if (isFromAirportOrAirline) {
            loadAd()
        }
    }

    private fun loadAd() {
        val NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline =
            RemoteConfigManager.getBoolean("NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline")
        if (NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline) {
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                nativeAdController.apply {
                    loadNativeAd(
                        requireContext(), app.getString(R.string.NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline)
                    )
                }
            }
        }

    }

    fun checkData() {
        if (arrivalData.isEmpty()) {
            (activity as AirportSearchActivity).binding.AirportName.invisible()
        } else {
            (activity as AirportSearchActivity).binding.AirportName.visible()
        }
    }

    private fun observeData() {
        binding.recyclerView.adapter = adapter
        arrivalFlightData.observe(viewLifecycleOwner) { arrivalData ->
            if (arrivalData.isEmpty()) {
                binding.conPlaceholder.visible()
                (activity as AirportSearchActivity).binding.AirportName.invisible()
            } else {
                if (!isFromAirportOrAirline) {
                    val NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber =
                        RemoteConfigManager.getBoolean("NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber")
                    if (NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber) {
                        binding.flAdplaceholder.visible()
                        val app = (requireActivity() as? BaseActivity<*>)?.app
                        app?.let {
                            binding.flAdplaceholder.visible()
                            nativeAdController.loadNativeAd(
                                requireContext(), it.getString(R.string.NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber)
                            )
                            nativeAdController.showNativeAd(
                                requireContext(),
                                binding.flAdplaceholder
                            )
                        }
                    }
                }
                this.arrivalData = arrivalData
                var arrData = if (isFromAirportOrAirline) {
                    addAdToArrivalData()
                } else {
                    arrivalData
                }
                adapter?.setList(arrData, nativeAdController)
                adapter?.setListener { arrivalData ->
                    showRewardedAd()
                    getFullArrivalFlightDataDetail(arrivalData)
                }
            }

            lifecycleScope.launch(Dispatchers.Main) {
                binding.pg.invisible()
                try {
                    searchedDataSubTitle = arrivalData.getOrNull(0)?.airlineName ?: "N/A"
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
                (activity as AirportSearchActivity).setAirportName()
            }
        }
    }

    private fun showRewardedAd() {
        val REWARDED_ARRIVAL =
            RemoteConfigManager.getBoolean("REWARDED_ARRIVAL")
        if (REWARDED_ARRIVAL) {
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                rewardedAd.loadAndShowRewardedAd(
                    requireActivity(),
                    app.getString(R.string.REWARDED_ARRIVAL),
                    onRewardEarned = {
                        startActivity(Intent(requireContext(), DetailActivity::class.java))
                    }, {
                        startActivity(Intent(requireContext(), DetailActivity::class.java))
                    }
                )
            }
        } else {
            startActivity(Intent(requireContext(), DetailActivity::class.java))
        }
    }

    private fun addAdToArrivalData(): ArrayList<ArrivalDataItems> {
        val arrData = ArrayList<ArrivalDataItems>()
        arrivalData.forEachIndexed { index, data ->
            val NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline =
                RemoteConfigManager.getBoolean("NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline")
            if (NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline) {
                if (index == 1) {
                    arrData.add(data.copy(type = 2))
                }
                if (index % 2 == 1 && index > 2) {
                    arrData.add(data.copy(type = 2))
                }
            }

            arrData.add(data.copy(type = 1))
        }
        return arrData
    }

    override fun onResume() {
        super.onResume()
        searchedDataSubTitle = arrivalData.getOrNull(0)?.airlineName ?: "N/A"
        (activity as AirportSearchActivity).setAirportName()
    }

    fun getFullArrivalFlightDataDetail(arrivalData: ArrivalDataItems) {
        var fullArrivalFlightDataDetails: FullDetailFlightData? = null

        fullArrivalFlightDataDetails = FullDetailFlightData(
            flightNo = arrivalData.flightNo,
            depIataCode = arrivalData.depIataCode,
            arrIataCode = arrivalData.arrIataCode,
            arrAirportName = arrivalData.arrAirportName,
            depAirportName = arrivalData.depAirportName,
            depCity = arrivalData.depCity,
            arrCity = arrivalData.arrCity,
            nameAirport = arrivalData.nameAirport,
            callSign = arrivalData.callSign,
            scheduledArrTime = arrivalData.scheduledArrTime,
            scheduledDepTime = arrivalData.scheduledDepTime,
            actualDepTime = arrivalData.actualDepTime,
            estimatedArrTime = arrivalData.estimatedArrivalTime,
            flightIataNumber = arrivalData.flightIataNumber,
            airlineName = arrivalData.airlineName,
            flightIcaoNo = arrivalData.flightIcaoNo,
            terminal = arrivalData.terminal,
            gate = arrivalData.gate,
            delay = arrivalData.delay,
            scheduled = arrivalData.scheduled,
            altitude = arrivalData.altitude,
            direction = arrivalData.direction,
            latitude = arrivalData.latitude,
            longitude = arrivalData.longitude,
            hSpeed = arrivalData.hSpeed,
            vSpeed = arrivalData.vSpeed,
            status = arrivalData.status,
            squawk = arrivalData.squawk,
            modelName = arrivalData.modelName,
            modelCode = arrivalData.modelCode,
            airCraftType = arrivalData.airCraftType,
            regNo = arrivalData.regNo,
            iataModel = arrivalData.iataModel,
            icaoHex = arrivalData.icaoHex,
            productionLine = arrivalData.productionLine,
            series = arrivalData.series,
            lineNumber = arrivalData.lineNumber,
            constructionNo = arrivalData.constructionNo,
            firstFlight = arrivalData.firstFlight,
            deliveryDate = arrivalData.deliveryDate,
            rolloutDate = arrivalData.rolloutDate,
            currentOwner = arrivalData.currentOwner,
            planeStatus = arrivalData.planeStatus,
            airLineIataCode = arrivalData.airLineIataCode,
            airLineICaoCode = arrivalData.airLineICaoCode,
            airPlaneIataCode = arrivalData.airPlaneIataCode,
            engineCount = arrivalData.engineCount,
            regDate = arrivalData.regDate,
        )

        FullDetailsFlightData = fullArrivalFlightDataDetails
    }
}