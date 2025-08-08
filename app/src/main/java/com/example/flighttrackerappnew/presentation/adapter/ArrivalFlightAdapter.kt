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
import com.example.flighttrackerappnew.presentation.listener.ArrivalListener

class ArrivalFlightAdapter : RecyclerView.Adapter<ArrivalFlightAdapter.SearchAirportViewHolders>() {
    private var arrivalFlight = ArrayList<ArrivalDataItems>()
    private var nativeAdController: NativeAdController? = null

    private var arrivalListener: ArrivalListener? = null
    fun setListener(arrivalListener: ArrivalListener) {
        this.arrivalListener = arrivalListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<ArrivalDataItems>, nativeAdController: NativeAdController) {
        arrivalFlight.clear()
        arrivalFlight = list
        this.nativeAdController = nativeAdController
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolders {
        return when (viewType) {
            1 -> {
                val binding = ArrivalFlightItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolders.Type1(binding)
            }

            2 -> {
                val binding = NativeAdLayoutViewWithMediaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolders.Type2(binding)
            }

            else -> {
                val binding = NativeAdLayoutViewWithMediaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SearchAirportViewHolders.Type2(binding)
            }
        }
    }

    override fun onBindViewHolder(
        holder: SearchAirportViewHolders,
        position: Int
    ) {
        when (holder) {
            is SearchAirportViewHolders.Type1 -> {
                holder.bind(position, arrivalFlight)
                holder.listener(position, arrivalFlight, arrivalListener)
            }

            is SearchAirportViewHolders.Type2 -> {
                holder.bind(nativeAdController)
            }
        }
    }

    override fun getItemCount(): Int = arrivalFlight.size

    override fun getItemViewType(position: Int): Int {
        return arrivalFlight[position].type
    }

    sealed class SearchAirportViewHolders(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        class Type1(private val binding: ArrivalFlightItemLayoutBinding) :
            SearchAirportViewHolders(binding.root) {

            fun bind(position: Int, arrivalFlight: ArrayList<ArrivalDataItems>) {
                val item = arrivalFlight[position]

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
                arrivalFlight: ArrayList<ArrivalDataItems>,
                arrivalListener: ArrivalListener?
            ) {
                binding.viewDetailsBtn.setOnClickListener {
                    arrivalListener?.onclick(arrivalFlight[position])
                }
            }
        }

        class Type2(private val binding: NativeAdLayoutViewWithMediaBinding) :
            SearchAirportViewHolders(binding.root) {
            fun bind(nativeAdController: NativeAdController?) {
                nativeAdController?.showNativeAd(binding.root.context, binding.root)
            }
        }
    }
}