package com.example.liveflighttrackerapp.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.databinding.SearchAirportItemLayoutBinding
import com.example.liveflighttrackerapp.presentation.listener.SearchAirportListener

class SearchAirportAdapter : RecyclerView.Adapter<SearchAirportAdapter.SearchAirportViewHolder>() {
    private var airPortList = ArrayList<AirportsDataItems>()

    private var listener: SearchAirportListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<AirportsDataItems>) {
        airPortList = list as ArrayList<AirportsDataItems>
        notifyDataSetChanged()
    }

    fun setListener(searchAirportListener: SearchAirportListener) {
        listener = searchAirportListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolder {
        val binding = SearchAirportItemLayoutBinding.inflate(
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

    override fun getItemCount(): Int = airPortList.size

    inner class SearchAirportViewHolder(private val binding: SearchAirportItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.airPortName.text = airPortList[position].nameAirport
        }

        fun listener(pos: Int) {
            binding.root.setOnClickListener {
                listener?.onAirportSelected(airPortList[pos])
            }
        }
    }
}