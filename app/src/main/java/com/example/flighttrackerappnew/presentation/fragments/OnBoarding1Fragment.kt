package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding1Binding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject

class OnBoarding1Fragment : Fragment() {

    private val nativeAdController: NativeAdController by inject()
    private val config: Config by inject()

    private val binding: FragmentOnBoarding1Binding by lazy {
        FragmentOnBoarding1Binding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val showOB1 =
            RemoteConfigManager.getBoolean("NATIVE_ONB1")
        val showOB4 =
            RemoteConfigManager.getBoolean("NATIVE_ONB4")
        val showOB2 =
            RemoteConfigManager.getBoolean("NATIVE_ONB2")

        val app = (requireActivity() as? BaseActivity<*>)?.app
        app?.let {
            if (showOB4 && !config.isPremiumUser) {
                nativeAdController.loadOnb4NativeAd(
                    requireContext(),
                    app.getString(R.string.NATIVE_ONB4)
                )
            }
            if (showOB2 && !config.isPremiumUser) {
                nativeAdController.loadOnb2NativeAd(
                    requireContext(),
                    app.getString(R.string.NATIVE_ONB4)
                )
            }
        }

        binding.apply {
            if (showOB1 && !config.isPremiumUser) {
                navBottom.visible()
                navTop.invisible()
                flAdplaceholder.visible()
                nativeAdController.showOnb1NativeAd(
                    requireContext(),
                    flAdplaceholder
                )
            } else {
                navTop.visible()
                navBottom.invisible()
            }
        }

        binding.apply {
            btnNext.setOnClickListener {
                (activity as? OnBoardingActivity)?.gotToNextPage()
            }
            conNext.setOnClickListener {
                (activity as? OnBoardingActivity)?.gotToNextPage()
            }
        }

    }
}