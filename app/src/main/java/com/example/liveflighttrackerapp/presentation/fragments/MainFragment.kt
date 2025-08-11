package com.example.liveflighttrackerapp.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.liveflighttrackerapp.R
import com.example.liveflighttrackerapp.databinding.FragmentMainBinding
import com.example.liveflighttrackerapp.databinding.FragmentMapBinding


class MainFragment : Fragment() {

    private val binding: FragmentMainBinding by lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
}