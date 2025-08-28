package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding3Binding
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject


class OnBoarding3Fragment : Fragment() {
    private val binding: FragmentOnBoarding3Binding by lazy {
        FragmentOnBoarding3Binding.inflate(layoutInflater)
    }
    private val config: Config by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnNext.setOnClickListener {
                (activity as? OnBoardingActivity)?.gotToNextPage()
            }
            conNext.setOnClickListener {
                (activity as? OnBoardingActivity)?.gotToNextPage()
            }
        }

        if (config.isPremiumUser) {
            binding.apply {
                navTop.visible()
                navBottom.invisible()
                lottie.invisible()
            }
        }
    }
}