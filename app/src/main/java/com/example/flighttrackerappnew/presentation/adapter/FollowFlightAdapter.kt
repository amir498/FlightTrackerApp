package com.example.flighttrackerappnew.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttrackerappnew.data.model.follow.FollowFlightData
import com.example.flighttrackerappnew.databinding.TrackedFlightItemsLayoutBinding
import com.example.flighttrackerappnew.presentation.listener.FollowedFlightListener
import com.example.flighttrackerappnew.presentation.utils.extractTime

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
                flightNum.text = item.flightNo
                depTime.text = item.actualDepTime.extractTime()
                arriTime.text = item.scheduledArrTime.extractTime()
                sea.text = item.depCity
                alt.text = item.arrCity
                callSign.text = item.callSign
                AirCraftiataNumber.text = item.flightIataNumber
                depIataCode.text = item.depIataCode
                depCityName.text = item.depCity
                depTime.text = item.actualDepTime.extractTime()
                arrivalIataCode.text = item.arrIataCode
                arrCityName.text = item.arrCity
                arriTime.text = item.estimatedArrTime.extractTime()
                depActualTime.text = item.actualDepTime.extractTime()
                arrEstimatedTime.text = item.estimatedArrTime.extractTime()
                time.text = item.actualDepTime.extractTime()
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