package com.example.liveflighttrackerapp.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.liveflighttrackerapp.data.model.arrival.ArrivalDataItems
import com.example.liveflighttrackerapp.databinding.ArrivalFlightItemLayoutBinding
import com.example.liveflighttrackerapp.presentation.listener.ArrivalListener

class ArrivalFlightAdapter : RecyclerView.Adapter<ArrivalFlightAdapter.SearchAirportViewHolder>() {
    private var arrivalFlight = ArrayList<ArrivalDataItems>()

    private var arrivalListener: ArrivalListener? = null
    fun setListener(arrivalListener: ArrivalListener) {
        this.arrivalListener = arrivalListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<ArrivalDataItems>) {
        arrivalFlight.clear()
        arrivalFlight = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolder {
        val binding = ArrivalFlightItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchAirportViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SearchAirportViewHolder,
        position: Int
    ) {
        holder.bind(position)
        holder.listener(position)
    }

    override fun getItemCount(): Int = arrivalFlight.size

    inner class SearchAirportViewHolder(private val binding: ArrivalFlightItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = arrivalFlight[position]

            binding.apply {
                depIataCode.text = item.depIataCode
                arrivalIataCode.text = item.arrIataCode
                flightNum.text = item.flightNo
                ArriAirPortName.text = item.arrAirport
                DepAirPortName.text = item.depAirport
                depCityName.text = item.depCity
                arrCityName.text = item.arrCity
            }
        }

        fun listener(position: Int) {
            binding.viewDetailsBtn.setOnClickListener {
                arrivalListener?.onclick(arrivalFlight[position])
            }
        }
    }
}