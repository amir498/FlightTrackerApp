package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding2Binding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject


class OnBoarding2Fragment : Fragment() {
    private val binding: FragmentOnBoarding2Binding by lazy {
        FragmentOnBoarding2Binding.inflate(layoutInflater)
    }
    private val config: Config by inject()

    private val nativeAdController: NativeAdController by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val showOBFull2 =
            RemoteConfigManager.getBoolean("NATIVE_ONB_Full2")

        val showOBFull1 =
            RemoteConfigManager.getBoolean("NATIVE_ONB_Full1")

        val showOB2 =
            RemoteConfigManager.getBoolean("NATIVE_ONB2")

        binding.conNext.setOnClickListener {
            (activity as? OnBoardingActivity)?.gotToNextPage()
        }

        binding.btnNext.setOnClickListener {
            (activity as? OnBoardingActivity)?.gotToNextPage()
        }
        if (showOBFull2 && !config.isPremiumUser) {
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                nativeAdController.loadFullNativeAd2(
                    requireContext(),
                    app.getString(R.string.NATIVE_ONB_Full2)
                )
            }
        } else {
            binding.lottie.invisible()
        }

        binding.apply {
            if (showOB2 && !config.isPremiumUser) {
                navTop.invisible()
                navBottom.visible()
                flAdplaceholder.visible()
                binding.lottie.invisible()
                nativeAdController.showOnb2NativeAd(
                    requireContext(),
                    flAdplaceholder
                )
            } else if (config.isPremiumUser) {
                navTop.visible()
                navBottom.invisible()
                binding.lottie.invisible()
                flAdplaceholder.invisible()
            } else if (showOBFull1) {
                navTop.invisible()
                navBottom.visible()
                flAdplaceholder.invisible()
            } else {
                lottie.invisible()
                navBottom.invisible()
                navTop.visible()
            }
        }
    }
}