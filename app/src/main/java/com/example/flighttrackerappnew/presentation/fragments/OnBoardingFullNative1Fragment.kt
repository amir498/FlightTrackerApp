package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.flighttrackerappnew.databinding.FragmentOnBoardingFullNative1Binding
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import org.koin.android.ext.android.inject
import kotlin.getValue


class OnBoardingFullNative1Fragment : Fragment() {

    private val binding: FragmentOnBoardingFullNative1Binding by lazy {
        FragmentOnBoardingFullNative1Binding.inflate(layoutInflater)
    }

    private val nativeAdController: NativeAdController by inject()

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
        nativeAdController.showFullNativeAd1(
            binding.flAdplaceholder,
            binding.nativeAdShimmer,
            requireContext()
        ) {

        }
    }
}