package com.example.liveflighttrackerapp.presentation.fragments.airportsearch.sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.liveflighttrackerapp.databinding.FragmentDepartureFlightBinding
import com.example.liveflighttrackerapp.presentation.adapter.DepartureFlightAdapter
import com.example.liveflighttrackerapp.presentation.utils.departureFlightData

class DepartureFlightFragment : Fragment() {

    private val binding: FragmentDepartureFlightBinding by lazy {
        FragmentDepartureFlightBinding.inflate(layoutInflater)
    }

    private var adapter: DepartureFlightAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = DepartureFlightAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        adapter?.setList(departureFlightData)
    }
}