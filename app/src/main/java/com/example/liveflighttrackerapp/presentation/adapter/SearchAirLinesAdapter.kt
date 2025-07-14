package com.example.liveflighttrackerapp.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.liveflighttrackerapp.data.model.airLine.StaticAirLineItems
import com.example.liveflighttrackerapp.databinding.SearchAirlineItemLayoutBinding
import com.example.liveflighttrackerapp.presentation.listener.SearchAirLineListener

class SearchAirLinesAdapter : RecyclerView.Adapter<SearchAirLinesAdapter.SearchAirportViewHolder>() {
    private var airLinesData = ArrayList<StaticAirLineItems>()

    private var listener: SearchAirLineListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<StaticAirLineItems>) {
        airLinesData = list as ArrayList<StaticAirLineItems>
        notifyDataSetChanged()
    }

    fun setListener(searchAirLineListener: SearchAirLineListener) {
        listener = searchAirLineListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolder {
        val binding = SearchAirlineItemLayoutBinding.inflate(
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

    override fun getItemCount(): Int = airLinesData.size

    inner class SearchAirportViewHolder(private val binding: SearchAirlineItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.airPortName.text = airLinesData[position].nameAirline
        }

        fun listener(pos: Int) {
            binding.root.setOnClickListener {
                listener?.onAirlineSelected(airLinesData[pos])
            }
        }
    }
}