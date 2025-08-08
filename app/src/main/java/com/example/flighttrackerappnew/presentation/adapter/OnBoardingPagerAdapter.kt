package com.example.flighttrackerappnew.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding1Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding2Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding3Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoarding4Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoardingFullNative1Fragment
import com.example.flighttrackerappnew.presentation.fragments.OnBoardingFullNative2Fragment

class OnBoardingPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentList: List<Fragment> by lazy {
        listOf(
            OnBoarding1Fragment(),
            OnBoarding2Fragment(),
            OnBoardingFullNative1Fragment(),
            OnBoarding3Fragment(),
            OnBoardingFullNative2Fragment(),
            OnBoarding4Fragment()
        )
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}
