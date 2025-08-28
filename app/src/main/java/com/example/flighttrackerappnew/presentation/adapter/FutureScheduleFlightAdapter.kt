package com.example.flighttrackerappnew.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttrackerappnew.data.model.futureSchedule.CustomFutureSchedule
import com.example.flighttrackerappnew.databinding.FutureFlightScheduleItemLayoutBinding
import com.example.flighttrackerappnew.databinding.NativeAdLayoutViewWithMediaBinding
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.listener.SearchAircraftListener
import com.example.flighttrackerappnew.presentation.utils.selectedDate

class FutureScheduleFlightAdapter :
    RecyclerView.Adapter<FutureScheduleFlightAdapter.SearchAirportViewHolder>() {
    private var flightData = ArrayList<CustomFutureSchedule>()

    private var listener: SearchAircraftListener? = null
    private var nativeAdController: NativeAdController? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<CustomFutureSchedule>, nativeAdController: NativeAdController) {
        flightData = list
        this.nativeAdController = nativeAdController
        notifyDataSetChanged()
    }

    fun setListener(searchAircraftListener: SearchAircraftListener) {
        listener = searchAircraftListener
    }

    override fun getItemViewType(position: Int): Int {
        return flightData[position].type
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolder {
        return when (viewType) {
            1 -> {
                val binding = FutureFlightScheduleItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolder.Type1(binding)
            }

            2 -> {
                val binding = NativeAdLayoutViewWithMediaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolder.Type2(binding)
            }

            else -> {
                val binding = NativeAdLayoutViewWithMediaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolder.Type2(binding)
            }

        }
    }

    override fun onBindViewHolder(
        holder: SearchAirportViewHolder,
        position: Int
    ) {
        when (holder) {
            is SearchAirportViewHolder.Type1 -> {
                holder.bind(position, flightData)
                holder.listener()
            }

            is SearchAirportViewHolder.Type2 -> {
                holder.bind(nativeAdController)
            }

            else -> {}
        }
    }

    override fun getItemCount(): Int = flightData.size

    sealed class SearchAirportViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        class Type1(private val binding: FutureFlightScheduleItemLayoutBinding) :
            SearchAirportViewHolder(binding.root) {
            fun bind(position: Int, flightData: ArrayList<CustomFutureSchedule>) {
                binding.apply {
                    flightNumber.text = flightData[position].flightNo
                    codeiatanumber.text = flightData[position].airLineIataCode
                    depCityIataCode.text = flightData[position].departureCityIataCode
                    arrCityIataCode.text = flightData[position].arrivalCityIataCode
                    arrCity.text = flightData[position].arrivalCity
                    depCity.text = flightData[position].departureCity
                    depTime.text = flightData[position].departureTime
                    arrTime.text = flightData[position].arrivalTime
                    FlightTime.text = flightData[position].flightTime
                    date.text = selectedDate
                }
            }

            fun listener() {
                binding.dropDown.setOnClickListener {
                    binding.conFlightDetails.visibility = if (binding.conFlightDetails.isVisible) {
                        binding.dropDown.rotation = 180f
                        ViewGroup.GONE
                    } else {
                        binding.dropDown.rotation = 0f
                        ViewGroup.VISIBLE
                    }
                }
            }
        }

        class Type2(private val binding: NativeAdLayoutViewWithMediaBinding) :
            SearchAirportViewHolder(binding.root) {
            fun bind(nativeAdController: NativeAdController?) {
                nativeAdController?.showNativeAd(binding.root.context, binding.root)
            }
        }

    }
}