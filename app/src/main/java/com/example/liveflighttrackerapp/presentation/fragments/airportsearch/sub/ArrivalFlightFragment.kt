package com.example.liveflighttrackerapp.presentation.fragments.airportsearch.sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.liveflighttrackerapp.data.model.arrival.ArrivalDataItems
import com.example.liveflighttrackerapp.data.model.fulldetails.FullDetailFlightData
import com.example.liveflighttrackerapp.databinding.FragmentArrivalFlightBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity
import com.example.liveflighttrackerapp.presentation.adapter.ArrivalFlightAdapter
import com.example.liveflighttrackerapp.presentation.utils.FullDetailsFlightData
import com.example.liveflighttrackerapp.presentation.utils.arrivalFlightData

class ArrivalFlightFragment : Fragment() {

    private val binding: FragmentArrivalFlightBinding by lazy {
        FragmentArrivalFlightBinding.inflate(layoutInflater)
    }

    private var adapter: ArrivalFlightAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = ArrivalFlightAdapter()
        return binding.root
    }

    fun setData() {
        binding.recyclerView.adapter = adapter
        adapter?.setList(arrivalFlightData)
        adapter?.setListener { arrivalData ->
            getFullArrivalFlightDataDetail(arrivalData)
            (activity as MainActivity).setClickListener("flightDetails")
        }
    }

    fun getFullArrivalFlightDataDetail(arrivalData: ArrivalDataItems) {
        var fullArrivalFlightDataDetails: FullDetailFlightData? = null

        fullArrivalFlightDataDetails = FullDetailFlightData(
            flightNo = arrivalData.flightNo,
            depIataCode = arrivalData.depIataCode,
            arrIataCode = arrivalData.arrIataCode,
            arrAirport = arrivalData.arrAirport,
            depAirport = arrivalData.depAirport,
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
            airLineICaoCode = arrivalData.airLineICaoCode
        )

        FullDetailsFlightData = fullArrivalFlightDataDetails
    }
}