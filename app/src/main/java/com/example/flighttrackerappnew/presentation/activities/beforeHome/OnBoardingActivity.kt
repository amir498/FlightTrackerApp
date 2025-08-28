package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.databinding.ActivityOnBoardingBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.adapter.OnBoardingPagerAdapter
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding1Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding2Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding3Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding4Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoardingFullNative1Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoardingFullNative2Fragment
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight

class OnBoardingActivity :
    BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val showOBFull1 =
            RemoteConfigManager.getBoolean("NATIVE_ONB_Full1")
        val showOBFull2 =
            RemoteConfigManager.getBoolean("NATIVE_ONB_Full2")
        val params = binding.viewPager.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.viewPager.layoutParams = params

        val adapter = OnBoardingPagerAdapter(this, getFragmentList(showOBFull2, showOBFull1))
        binding.viewPager.adapter = adapter
    }

    fun gotToNextPage(){
        val nextItem = binding.viewPager.currentItem + 1
        binding.viewPager.setCurrentItem(nextItem, true)
    }

    fun getFragmentList(showOBFull2: Boolean, showOBFull1: Boolean): List<Fragment> {
        return if (config.isPremiumUser) {
            return listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoarding3Fragment(),
                OnBoarding4Fragment()
            )
        } else if (showOBFull1 && showOBFull2) {
            listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoardingFullNative1Fragment(),
                OnBoarding3Fragment(),
                OnBoardingFullNative2Fragment(),
                OnBoarding4Fragment()
            )
        } else if (showOBFull1) {
            listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoardingFullNative1Fragment(),
                OnBoarding3Fragment(),
                OnBoarding4Fragment()
            )
        } else if (showOBFull2) {
            listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoarding3Fragment(),
                OnBoardingFullNative2Fragment(),
                OnBoarding4Fragment()
            )
        } else {
            listOf(
                OnBoarding1Fragment(),
                OnBoarding2Fragment(),
                OnBoarding3Fragment(),
                OnBoarding4Fragment()
            )
        }
    }
}