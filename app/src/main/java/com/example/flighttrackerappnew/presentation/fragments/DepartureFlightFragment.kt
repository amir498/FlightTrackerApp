package com.example.flighttrackerappnew.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.arrival.ArrivalDataItems
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.databinding.FragmentDepartureFlightBinding
import com.example.flighttrackerappnew.presentation.activities.AirportSearchActivity
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.DetailActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.adapter.DepartureFlightAdapter
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.departureFlightData
import com.example.flighttrackerappnew.presentation.utils.getFlightProgressPercent
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.searchedDataSubTitle
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject

class DepartureFlightFragment : Fragment() {

    private val binding: FragmentDepartureFlightBinding by lazy {
        FragmentDepartureFlightBinding.inflate(layoutInflater)
    }

    private var adapter: DepartureFlightAdapter? = null
    private var departureData = ArrayList<ArrivalDataItems>()
    private val nativeAdController: NativeAdController by inject()
    private val rewardedAd: RewardedAdManager by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        adapter = DepartureFlightAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFromAirportOrAirline && !config.isPremiumUser) {
            loadAd()
        }
    }

    private fun loadAd() {
        val NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline =
            RemoteConfigManager.getBoolean("NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline")
        if (NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline) {
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                nativeAdController.apply {
                    loadNativeAd(
                        requireContext(),
                        app.getString(R.string.NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline)
                    )
                }
            }
        }

    }

    fun checkData() {
        if (departureData.isEmpty()) {
            (activity as AirportSearchActivity).binding.AirportName.invisible()
        } else {
            (activity as AirportSearchActivity).binding.AirportName.visible()
        }
    }

    private fun observeData() {
        binding.recyclerView.adapter = adapter
        departureFlightData.observe(viewLifecycleOwner) { departureData ->
            if (departureData.isEmpty()) {
                binding.conPlaceholder.visible()
                binding.pg.invisible()
                (activity as AirportSearchActivity).binding.AirportName.invisible()
                binding.recyclerView.invisible()
            } else {
                if (!isFromAirportOrAirline && !config.isPremiumUser) {
                    val app = (requireActivity() as? BaseActivity<*>)?.app
                    app?.let {
                        val NATIVE_DEPARTURE_FLIGHT_For_Aircraft_Or_TailNumber =
                            RemoteConfigManager.getBoolean("NATIVE_DEPARTURE_FLIGHT_For_Aircraft_Or_TailNumber")
                        if (NATIVE_DEPARTURE_FLIGHT_For_Aircraft_Or_TailNumber) {
                            binding.flAdplaceholder.visible()
                            nativeAdController.loadNativeAd(
                                requireContext(),
                                it.getString(R.string.NATIVE_DEPARTURE_FLIGHT_For_Aircraft_Or_TailNumber)
                            )
                            nativeAdController.showNativeAd(
                                requireContext(),
                                binding.flAdplaceholder
                            )
                        }
                    }
                }
                this.departureData = departureData
                var arrData = if (isFromAirportOrAirline && !config.isPremiumUser) {
                    addAdToDepartureData()
                } else {
                    departureData
                }
                adapter?.setList(arrData, nativeAdController)
                adapter?.setListener { arrivalData ->
                    if ((requireActivity() as BaseActivity<*>).config.isPremiumUser) {
                        startActivity(Intent(requireContext(), DetailActivity::class.java))
                    } else {
                        showDialogPremium()
                    }
                    getFullDepartureFlightDataDetail(arrivalData)
                }
                binding.pg.invisible()
                binding.conPlaceholder.invisible()
                try {
                    searchedDataSubTitle = departureData?.getOrNull(0)?.airlineName ?: "N/A"
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
                (activity as AirportSearchActivity).setAirportName()
            }
        }
    }

    private val config: Config by inject()
    private fun showDialogPremium() {
        CustomDialogBuilder(requireContext())
            .setLayout(R.layout.dialog_premium)
            .setCancelable(false)
            .setPositiveClickListener {
                val intent = Intent(requireContext(), PremiumActivity::class.java)
                intent.putExtra("from_arrival", true)
                startActivity(intent)
                it.dismiss()
            }.setNegativeClickListener {
                showRewardedAd()
                it.dismiss()
            }.setCrossBtnListener {
                it.dismiss()
            }
            .show()
    }

    private fun showRewardedAd() {
        val REWARDED_DEPARTURE =
            RemoteConfigManager.getBoolean("REWARDED_DEPARTURE")
        if (REWARDED_DEPARTURE) {
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                rewardedAd.loadAndShowRewardedAd(
                    requireActivity(),
                    app.getString(R.string.REWARDED_DEPARTURE),
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

    private fun addAdToDepartureData(): ArrayList<ArrivalDataItems> {
        val arrData = ArrayList<ArrivalDataItems>()
        departureData.forEachIndexed { index, data ->
            val NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline =
                RemoteConfigManager.getBoolean("NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline")
            if (NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline) {
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

        observeData()
        searchedDataSubTitle = departureData.getOrNull(0)?.airlineName ?: "N/A"
        (activity as AirportSearchActivity).setAirportName()
    }

    fun getFullDepartureFlightDataDetail(departureData: ArrivalDataItems) {
        var fullArrivalFlightDataDetails: FullDetailFlightData? = null
        val progress =
            getFlightProgressPercent(
                departureData.actualDepTime,
                departureData.estimatedArrivalTime
            )
        fullArrivalFlightDataDetails = FullDetailFlightData(
            flightNo = departureData.flightNo,
            depIataCode = departureData.depIataCode,
            arrIataCode = departureData.arrIataCode,
            arrAirportName = departureData.arrAirportName,
            depAirportName = departureData.depAirportName,
            depCity = departureData.depCity,
            arrCity = departureData.arrCity,
            nameAirport = departureData.nameAirport,
            callSign = departureData.callSign,
            scheduledArrTime = departureData.scheduledArrTime,
            scheduledDepTime = departureData.scheduledDepTime,
            actualDepTime = departureData.actualDepTime,
            estimatedArrTime = departureData.estimatedArrivalTime,
            flightIataNumber = departureData.flightIataNumber,
            airlineName = departureData.airlineName,
            flightIcaoNo = departureData.flightIcaoNo,
            terminal = departureData.terminal,
            gate = departureData.gate,
            delay = departureData.delay,
            scheduled = departureData.scheduled,
            altitude = departureData.altitude,
            direction = departureData.direction,
            latitude = departureData.latitude,
            longitude = departureData.longitude,
            hSpeed = departureData.hSpeed,
            vSpeed = departureData.vSpeed,
            status = departureData.status,
            squawk = departureData.squawk,
            modelName = departureData.modelName,
            modelCode = departureData.modelCode,
            airCraftType = departureData.airCraftType,
            regNo = departureData.regNo,
            iataModel = departureData.iataModel,
            icaoHex = departureData.icaoHex,
            productionLine = departureData.productionLine,
            series = departureData.series,
            lineNumber = departureData.lineNumber,
            constructionNo = departureData.constructionNo,
            firstFlight = departureData.firstFlight,
            deliveryDate = departureData.deliveryDate,
            rolloutDate = departureData.rolloutDate,
            currentOwner = departureData.currentOwner,
            planeStatus = departureData.planeStatus,
            airLineIataCode = departureData.airLineIataCode,
            airLineICaoCode = departureData.airLineICaoCode,
            airPlaneIataCode = departureData.airPlaneIataCode,
            engineCount = departureData.engineCount,
            regDate = departureData.regDate,
            progress = progress
        )

        FullDetailsFlightData = fullArrivalFlightDataDetails
    }
}