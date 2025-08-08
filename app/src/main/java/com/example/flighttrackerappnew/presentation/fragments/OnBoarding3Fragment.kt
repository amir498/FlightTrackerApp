package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding2Binding
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding3Binding
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding4Binding
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight


class OnBoarding3Fragment : Fragment() {
    private val binding: FragmentOnBoarding3Binding by lazy {
        FragmentOnBoarding3Binding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            (activity as? OnBoardingActivity)?.let { onboardingActivity ->
                val nextItem = onboardingActivity.binding.viewPager.currentItem + 1
                onboardingActivity.binding.viewPager.setCurrentItem(nextItem, true)
            }
        }
    }
}