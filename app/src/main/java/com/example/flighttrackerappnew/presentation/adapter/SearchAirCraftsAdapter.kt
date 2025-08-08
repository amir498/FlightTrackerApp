package com.example.flighttrackerappnew.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.databinding.SearchAircraftItemLayoutBinding
import com.example.flighttrackerappnew.presentation.listener.SearchAircraftListener

class SearchAirCraftsAdapter : RecyclerView.Adapter<SearchAirCraftsAdapter.SearchAirportViewHolder>() {
    private var flightData = ArrayList<FlightDataItem>()

    private var listener: SearchAircraftListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<FlightDataItem>) {
        flightData = ArrayList(list)
        notifyDataSetChanged()
    }

    fun setListener(searchAircraftListener: SearchAircraftListener) {
        listener = searchAircraftListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolder {
        val binding = SearchAircraftItemLayoutBinding.inflate(
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

    override fun getItemCount(): Int = flightData.size

    inner class SearchAirportViewHolder(private val binding: SearchAircraftItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.airPortName.text = flightData[position].flight?.iataNumber
        }

        fun listener(pos: Int) {
            binding.root.setOnClickListener {
                listener?.onAircraftSelected(flightData[pos])
            }
        }
    }
}