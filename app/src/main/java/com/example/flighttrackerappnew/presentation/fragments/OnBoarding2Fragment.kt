package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding2Binding
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_WELCOME
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_OnBoarding2
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewListener()
        setLayout()
        loadAd()
    }

    private fun setLayout() {
        binding.apply {
            if (RemoteConfigManager.getBoolean("native_OnBoarding2") && !config.isPremiumUser) {
                navTop.invisible()
                navBottom.visible()
                native_OnBoarding2.loadNativeAd(
                    requireContext(),
                    RemoteConfigManager.getBoolean("native_OnBoarding2")
                )
                native_OnBoarding2.showNativeAd(
                    adGroup = native_OnBoarding2,
                    frameLayout = binding.flAdplaceholder,
                    adLayout = R.layout.native_ad_layout_view_with_media,
                    activity =  requireActivity() as AppCompatActivity
                )
                lottie.invisible()
            } else if (config.isPremiumUser) {
                navTop.visible()
                navBottom.invisible()
                binding.lottie.invisible()
            } else if (RemoteConfigManager.getBoolean("native_OnBoarding3")) {
                navTop.invisible()
                navBottom.visible()
                lottie.visible()
            } else {
                lottie.invisible()
                navBottom.invisible()
                navTop.visible()
            }
        }
    }

    private fun viewListener() {
        binding.apply {
            conNext.setOnClickListener {
                (activity as? OnBoardingActivity)?.gotToNextPage()
            }

            btnNext.setOnClickListener {
                (activity as? OnBoardingActivity)?.gotToNextPage()
            }
        }
    }

    private fun loadAd() {
        NATIVE_WELCOME.loadNativeAd(
            requireContext(),
            RemoteConfigManager.getBoolean("NATIVE_WELCOME")
        )
    }
}