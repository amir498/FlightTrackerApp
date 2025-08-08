package com.example.flighttrackerappnew.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttrackerappnew.data.model.FollowFlightData
import com.example.flighttrackerappnew.databinding.TrackedFlightItemsLayoutBinding
import com.example.flighttrackerappnew.presentation.listener.FollowedFlightListener

class FollowFlightAdapter : RecyclerView.Adapter<FollowFlightAdapter.SearchAirportViewHolder>() {
    private var list = ArrayList<FollowFlightData>()

    private var listener: FollowedFlightListener? = null
    fun setListener(followedFlightListener: FollowedFlightListener) {
        this.listener = followedFlightListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<FollowFlightData>) {
        this.list = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolder {
        val binding = TrackedFlightItemsLayoutBinding.inflate(
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

    override fun getItemCount(): Int = list.size

    inner class SearchAirportViewHolder(private val binding: TrackedFlightItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(position: Int) {
            val item = list[position]

            binding.apply {
                flightNum.text = item.flightNum
                depTime.text = item.depTime
                arriTime.text = item.arrivalTime
                sea.text = item.depCityName
                alt.text = item.arrivalCityName
                callSign.text = item.callSign
                AirCraftiataNumber.text = item.airCraftIataNumber
                depIataCode.text = item.depIataCode
                depCityName.text = item.depCityName
                depTime.text = item.depTime
                arrivalIataCode.text = item.arrivalIataCode
                arrCityName.text = item.arrivalCityName
                arriTime.text = item.arrivalTime
                depActualTime.text = item.depTime
                arrEstimatedTime.text = item.arrivalTime
                time.text = item.time
                discreteSeekBar.progress = item.progress
                binding.discreteSeekBar.setOnTouchListener { _, _ -> true }
            }
        }

        fun listener(position: Int) {
            binding.viewDetails.setOnClickListener {
                listener?.onViewDetailedClicked(list[position])
            }
            binding.unfollow.setOnClickListener {
                listener?.onUnFollowClicked(list[position])
            }
        }
    }
}