package com.example.liveflighttrackerapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.liveflighttrackerapp.databinding.FragmentTrackedFlightBinding

class TrackedFlightFragment : Fragment() {
    private val binding: FragmentTrackedFlightBinding by lazy {
        FragmentTrackedFlightBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}