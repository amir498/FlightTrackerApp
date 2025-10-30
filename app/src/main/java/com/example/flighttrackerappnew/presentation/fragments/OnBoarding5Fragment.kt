package com.example.flighttrackerappnew.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding5Binding
import com.example.flighttrackerappnew.presentation.activities.beforeHome.OnBoardingActivity
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_OnBoarding5


class OnBoarding5Fragment : Fragment() {
    private val binding: FragmentOnBoarding5Binding by lazy {
        FragmentOnBoarding5Binding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showAd()
        viewListener()
    }

    private fun showAd() {
        native_OnBoarding5.showNativeAd(
            adGroup = native_OnBoarding5,
            frameLayout = binding.flAdplaceholder,
            adLayout = R.layout.native_ad_layout_view_with_media_full,
          activity =   requireActivity() as AppCompatActivity
        )
    }

    private fun viewListener() {
        binding.btnNext.setOnClickListener {
            (activity as? OnBoardingActivity)?.let { onboardingActivity ->
                val nextItem = onboardingActivity.binding.viewPager.currentItem + 1
                onboardingActivity.binding.viewPager.setCurrentItem(nextItem, true)
            }
        }
    }
}