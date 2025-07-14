package com.example.liveflighttrackerapp.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.liveflighttrackerapp.presentation.fragments.airportsearch.sub.ArrivalFlightFragment
import com.example.liveflighttrackerapp.presentation.fragments.airportsearch.sub.DepartureFlightFragment

class AirportSearchPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentList: List<Fragment> by lazy {
        listOf(
            ArrivalFlightFragment(),
            DepartureFlightFragment()
        )
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}
