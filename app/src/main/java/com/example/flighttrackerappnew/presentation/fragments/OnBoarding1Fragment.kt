package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding1Binding
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_OnBoarding1
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_OnBoarding6
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject

class OnBoarding1Fragment : Fragment() {

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

        setLayout()
        loadAndShowAd()
        viewListener()
    }

    private fun setLayout() {
        binding.apply {
            if (RemoteConfigManager.getBoolean("native_OnBoarding1") && !config.isPremiumUser) {
                navBottom.visible()
                navTop.invisible()
                native_OnBoarding1.showNativeAd(
                    adGroup = native_OnBoarding1,
                    frameLayout = binding.flAdplaceholder,
                    adLayout = R.layout.native_ad_layout_view_with_media,
                    activity =  requireActivity() as AppCompatActivity
                )
            } else {
                navTop.visible()
                navBottom.invisible()
            }
        }
    }

    private fun viewListener() {
        binding.apply {
            btnNext.setOnClickListener {
                (activity as? OnBoardingActivity)?.gotToNextPage()
            }
            conNext.setOnClickListener {
                (activity as? OnBoardingActivity)?.gotToNextPage()
            }
        }
    }

    private fun loadAndShowAd() {
        native_OnBoarding6.loadNativeAd(
            requireContext(),
            RemoteConfigManager.getBoolean("native_OnBoarding6")
        )
    }
}