package com.example.flighttrackerappnew.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.databinding.FavFlightItemLayoutBinding
import com.example.flighttrackerappnew.presentation.listener.FavFlightListener

class FavFlightAdapter : RecyclerView.Adapter<FavFlightAdapter.SearchAirportViewHolder>() {
    private var list = ArrayList<FullDetailFlightData>()

    private var listener: FavFlightListener? = null
    fun setListener(favFlightListener: FavFlightListener) {
        this.listener = favFlightListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<FullDetailFlightData>) {
        this.list = ArrayList(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAirportViewHolder {
        val binding = FavFlightItemLayoutBinding.inflate(
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
        holder.apply {
            bind(position)
            listener(position)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class SearchAirportViewHolder(private val binding: FavFlightItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = list[position]
            binding.apply {
                flightNum.text = item.flightNo
                depTime.text = item.scheduledDepTime
                arriTime.text = item.scheduledArrTime
                sea.text = item.depCity
                alt.text = item.arrCity
                callSign.text = item.callSign
                AirCraftiataNumber.text = item.airPlaneIataCode
            }
        }

        fun listener(position: Int) {
            binding.apply {
                viewDetails.setOnClickListener {
                    listener?.onViewDetailedClicked(list[position])
                }
                unfollow.setOnClickListener {
                    listener?.onUnFavClicked(list[position])
                }
            }
        }
    }
}