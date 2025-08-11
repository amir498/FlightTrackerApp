package com.example.liveflighttrackerapp.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.liveflighttrackerapp.presentation.fragments.MapFragment
import com.example.liveflighttrackerapp.presentation.fragments.ProFragment
import com.example.liveflighttrackerapp.presentation.fragments.SearchFragment
import com.example.liveflighttrackerapp.presentation.fragments.SettingFragment

class MainActivityViewPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentList: List<Fragment> by lazy {
        listOf(
            MapFragment(),
            SearchFragment(),
            ProFragment(),
            SettingFragment()
        )
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}
