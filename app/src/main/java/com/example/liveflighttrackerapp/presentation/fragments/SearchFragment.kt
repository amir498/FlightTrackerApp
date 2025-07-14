package com.example.liveflighttrackerapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.liveflighttrackerapp.databinding.FragmentSearchBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity

class SearchFragment : Fragment() {

    private val binding: FragmentSearchBinding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewListener()
    }

    private fun viewListener() {
        binding.apply {
            conAirports.setOnClickListener {
                (activity as MainActivity).setClickListener("airport")
            }
            conAirCrafts.setOnClickListener {
                (activity as MainActivity).setClickListener("aircraft")
            }
            conAirLines.setOnClickListener {
                (activity as MainActivity).setClickListener("airline")
            }
            conTailNumbers.setOnClickListener {
                (activity as MainActivity).setClickListener("tail")
            }
            conNearby.setOnClickListener {
                (activity as MainActivity).setClickListener("nearby")
            }
        }
    }

}