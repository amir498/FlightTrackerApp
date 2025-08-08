package com.example.flighttrackerappnew.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttrackerappnew.data.model.arrival.ArrivalDataItems
import com.example.flighttrackerappnew.databinding.ArrivalFlightItemLayoutBinding
import com.example.flighttrackerappnew.databinding.NativeAdLayoutViewWithMediaBinding
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.listener.DepartureListener

class DepartureFlightAdapter :
    RecyclerView.Adapter<DepartureFlightAdapter.SearchAirportViewHolderss>() {
    private var departureFlight = ArrayList<ArrivalDataItems>()
    private var nativeAdController: NativeAdController? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<ArrivalDataItems>, nativeAdController: NativeAdController) {
        departureFlight.clear()
        departureFlight = list
        this.nativeAdController = nativeAdController
        notifyDataSetChanged()
    }

    private var departureListener: DepartureListener? = null
    fun setListener(departureListener: DepartureListener) {
        this.departureListener = departureListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolderss {
        return when (viewType) {
            1 -> {
                val binding = ArrivalFlightItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolderss.Type1(binding)
            }

            2 -> {
                val binding = NativeAdLayoutViewWithMediaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolderss.Type2(binding)
            }

            else -> {
                val binding = NativeAdLayoutViewWithMediaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolderss.Type2(binding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: SearchAirportViewHolderss,
        position: Int
    ) {
        when (holder) {
            is SearchAirportViewHolderss.Type1 -> {
                holder.bind(position, departureFlight)
                holder.listener(position, departureFlight, departureListener)
            }

            is SearchAirportViewHolderss.Type2 -> {
                holder.bind(nativeAdController)
            }
        }
    }

    override fun getItemCount(): Int = departureFlight.size

    override fun getItemViewType(position: Int): Int {
        return departureFlight[position].type
    }

    sealed class SearchAirportViewHolderss(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        class Type1(private val binding: ArrivalFlightItemLayoutBinding) :
            SearchAirportViewHolderss(binding.root) {
            fun bind(position: Int, departureFlight: ArrayList<ArrivalDataItems>) {
                val item = departureFlight[position]

                binding.apply {
                    depIataCode.text = item.depIataCode
                    arrivalIataCode.text = item.arrIataCode
                    flightNum.text = item.flightNo
                    ArriAirPortName.text = item.arrAirportName
                    DepAirPortName.text = item.depAirportName
                    depCityName.text = item.depCity
                    arrCityName.text = item.arrCity
                }
            }

            fun listener(
                position: Int,
                departureFlight: ArrayList<ArrivalDataItems>,
                departureListener: DepartureListener?
            ) {
                binding.viewDetailsBtn.setOnClickListener {
                    departureListener?.onclick(departureFlight[position])
                }
            }
        }

        class Type2(private val binding: NativeAdLayoutViewWithMediaBinding) :
            SearchAirportViewHolderss(binding.root) {
            fun bind(nativeAdController: NativeAdController?) {
                nativeAdController?.showNativeAd(binding.root.context, binding.root)
            }
        }
    }
}