package com.example.liveflighttrackerapp.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.liveflighttrackerapp.data.model.airplane.AirPlaneItems
import com.example.liveflighttrackerapp.databinding.SearchTailItemLayoutBinding
import com.example.liveflighttrackerapp.presentation.listener.SearchTailNumberListener

class TailSearchAdapter : RecyclerView.Adapter<TailSearchAdapter.SearchAirportViewHolder>() {
    private var airPlanesData = ArrayList<AirPlaneItems>()

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<AirPlaneItems>) {
        airPlanesData.clear()
        airPlanesData = list as ArrayList<AirPlaneItems>
        notifyDataSetChanged()
    }

    private var listener: SearchTailNumberListener? = null

    fun setListener(searchTailNumberListener: SearchTailNumberListener) {
        listener = searchTailNumberListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolder {
        val binding = SearchTailItemLayoutBinding.inflate(
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

    override fun getItemCount(): Int = airPlanesData.size

    inner class SearchAirportViewHolder(private val binding: SearchTailItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.airPortName.text = airPlanesData[position].numberRegistration
        }

        fun listener(pos: Int) {
            binding.root.setOnClickListener {
                listener?.onTailSelected(airPlanesData[pos])
            }
        }
    }
}