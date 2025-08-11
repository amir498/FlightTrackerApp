package com.example.liveflighttrackerapp.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.liveflighttrackerapp.presentation.fragments.OnBoarding1Fragment
import com.example.liveflighttrackerapp.presentation.fragments.OnBoarding2Fragment
import com.example.liveflighttrackerapp.presentation.fragments.OnBoarding3Fragment
import com.example.liveflighttrackerapp.presentation.fragments.OnBoarding4Fragment
import com.example.liveflighttrackerapp.presentation.fragments.OnBoarding5Fragment
import com.example.liveflighttrackerapp.presentation.fragments.OnBoarding6Fragment

class OnBoardingPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentList: List<Fragment> by lazy {
        listOf(
            OnBoarding1Fragment(),
            OnBoarding2Fragment(),
            OnBoarding3Fragment(),
            OnBoarding4Fragment(),
            OnBoarding5Fragment(),
            OnBoarding6Fragment(),
        )
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}
