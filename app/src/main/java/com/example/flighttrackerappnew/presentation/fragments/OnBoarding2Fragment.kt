package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding1Binding
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding2Binding
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding3Binding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import org.koin.android.ext.android.inject
import kotlin.getValue


class OnBoarding2Fragment : Fragment() {
    private val binding: FragmentOnBoarding2Binding by lazy {
        FragmentOnBoarding2Binding.inflate(layoutInflater)
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

        val showOBFull2 =
            RemoteConfigManager.getBoolean("NATIVE_ONB_Full2")

        binding.btnNext.setOnClickListener {
            (activity as? OnBoardingActivity)?.let { onboardingActivity ->
                val nextItem = onboardingActivity.binding.viewPager.currentItem + 1
                onboardingActivity.binding.viewPager.setCurrentItem(nextItem, true)
            }
        }
        if (showOBFull2) {
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                nativeAdController.loadFullNativeAd2(
                    requireContext(),
                    app.getString(R.string.NATIVE_ONB_Full2)
                )
            }
        }
    }
}