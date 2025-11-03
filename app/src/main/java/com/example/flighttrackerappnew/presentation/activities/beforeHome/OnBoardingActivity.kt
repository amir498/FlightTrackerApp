package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.databinding.ActivityOnBoardingBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.adapter.OnBoardingPagerAdapter
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_OnBoarding5
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding1Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding2Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding3Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding4Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding5Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding6Fragment
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager

class OnBoardingActivity :
    BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val showOBFull1 =
            RemoteConfigManager.getBoolean("native_OnBoarding3")
        val showOBFull2 =
            RemoteConfigManager.getBoolean("native_OnBoarding5")

        val adapter = OnBoardingPagerAdapter(this, getFragmentList(showOBFull2, showOBFull1))
        binding.viewPager.adapter = adapter

        loadAd()
    }

    private fun loadAd() {
        native_OnBoarding5.loadNativeAd(
            this@OnBoardingActivity,
            RemoteConfigManager.getBoolean("native_OnBoarding5")
        )
    }

    fun gotToNextPage() {
        val nextItem = binding.viewPager.currentItem + 1
        binding.viewPager.setCurrentItem(nextItem, true)
    }

    fun getFragmentList(showOBFull2: Boolean, showOBFull1: Boolean): List<Fragment> {
        return if (config.isPremiumUser) {
            return listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoarding4Fragment(),
                OnBoarding6Fragment()
            )
        } else if (showOBFull1 && showOBFull2) {
            listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoarding3Fragment(),
                OnBoarding4Fragment(),
                OnBoarding5Fragment(),
                OnBoarding6Fragment()
            )
        } else if (showOBFull1) {
            listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoarding3Fragment(),
                OnBoarding4Fragment(),
                OnBoarding6Fragment()
            )
        } else if (showOBFull2) {
            listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoarding4Fragment(),
                OnBoarding5Fragment(),
                OnBoarding6Fragment()
            )
        } else {
            listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoarding4Fragment(),
                OnBoarding6Fragment()
            )
        }
    }
}