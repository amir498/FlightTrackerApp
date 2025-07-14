package com.example.liveflighttrackerapp.presentation.fragments.airportsearch

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.liveflighttrackerapp.R
import com.example.liveflighttrackerapp.databinding.FragmentAirportSearchBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity
import com.example.liveflighttrackerapp.presentation.adapter.AirportSearchPagerAdapter
import com.example.liveflighttrackerapp.presentation.fragments.MyFragment
import com.example.liveflighttrackerapp.presentation.fragments.airportsearch.sub.ArrivalFlightFragment
import com.example.liveflighttrackerapp.presentation.utils.arrivalFlightData
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AirportSearchFragment(context: Context, attr: AttributeSet) :
    MyFragment<FragmentAirportSearchBinding>(context, attr) {

    private lateinit var adapter: AirportSearchPagerAdapter

    override fun setupFragment(activity: MainActivity) {
        this.activity = activity
        this.binding = FragmentAirportSearchBinding.bind(this)
        binding.btnBack.setOnClickListener {
            activity.onBackPress()
        }

        adapter = AirportSearchPagerAdapter(activity)
        binding.viewPager2.adapter = adapter

        setTabWithViewpager()

    }

    private fun setTabWithViewpager() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Arrivals"
                    tab.view.setBackgroundResource(R.drawable.search_airport_s_tab_bg)
                }

                1 -> {
                    tab.text = "Departures"
                    tab.view.setBackgroundResource(R.drawable.search_airport_uns_tab_bg)
                }
            }
        }.attach()
        tabListener()

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val layoutParams = tab.layoutParams as MarginLayoutParams
            layoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen.tab_padding)
            layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen.tab_padding)
            tab.requestLayout()
        }
    }

    private fun tabListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.search_airport_s_tab_bg)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.search_airport_uns_tab_bg)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    fun setData(title: String, airportNameVisibility: Boolean) {
        try {
            binding.AirportName.visibility = if (airportNameVisibility) VISIBLE else GONE
            binding.tvTitle.text = title
            binding.AirportName.text = arrivalFlightData[0].nameAirport
            val currentItem = binding.viewPager2.currentItem
            val fragmentTag = "f$currentItem"
            val frag: Fragment? = activity?.supportFragmentManager?.findFragmentByTag(fragmentTag)
            when (frag) {
                is ArrivalFlightFragment -> {
                    frag.setData()
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }
}