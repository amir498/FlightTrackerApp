package com.example.flighttrackerappnew.presentation.activities

import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityAirportSearchBinding
import com.example.flighttrackerappnew.presentation.adapter.AirportSearchPagerAdapter
import com.example.flighttrackerappnew.presentation.fragments.ArrivalFlightFragment
import com.example.flighttrackerappnew.presentation.fragments.DepartureFlightFragment
import com.example.flighttrackerappnew.presentation.utils.favData
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.isComeFromFav
import com.example.flighttrackerappnew.presentation.utils.isComeFromTracked
import com.example.flighttrackerappnew.presentation.utils.searchedDataSubTitle
import com.example.flighttrackerappnew.presentation.utils.searchedDataTitle
import com.example.flighttrackerappnew.presentation.utils.trackData
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AirportSearchActivity :
    BaseActivity<ActivityAirportSearchBinding>(ActivityAirportSearchBinding::inflate) {
    private lateinit var adapter: AirportSearchPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isComeFromFav = false
        isComeFromTracked = false
        trackData = null
        favData = null
        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        adapter = AirportSearchPagerAdapter(this)
        binding.viewPager2.adapter = adapter

        setTabWithViewpager()
        viewListener()
        changeTabsFont()
        viewpagerListener()
    }

    private fun viewpagerListener() {
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val fragmentTag = "f${binding.viewPager2.currentItem}"
                val fragment =
                    supportFragmentManager.findFragmentByTag(fragmentTag)
                when (fragment) {
                    is ArrivalFlightFragment -> {
                        fragment.checkData()
                    }

                    is DepartureFlightFragment -> {
                        fragment.checkData()
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })

    }

    private fun viewListener() {
        binding.btnBack.setOnClickListener {
            this.finish()
        }
    }

    private fun changeTabsFont() {
        val typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_bold) ?: return
        val vg = binding.tabLayout.getChildAt(0) as? ViewGroup ?: return
        val tabsCount = vg.childCount

        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as? ViewGroup ?: continue
            val tabChildsCount = vgTab.childCount

            for (i in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {
                    tabViewChild.typeface = typeface
                }
            }
        }
    }

    fun setAirportName() {
        binding.apply {
            AirportName.text = searchedDataSubTitle
            tvTitle.text = searchedDataTitle
        }
    }

    private fun setTabWithViewpager() {
        val typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_bold)
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Arrivals"
                    tab.view.setBackgroundResource(R.drawable.search_airport_s_tab_bg)
                    setTabFont(tab, typeface)
                }

                1 -> {
                    tab.text = "Departures"
                    tab.view.setBackgroundResource(R.drawable.search_airport_uns_tab_bg)
                    setTabFont(tab, typeface)
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
        val typeface = ResourcesCompat.getFont(this, R.font.sf_pro_display_bold)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                tab?.view?.setBackgroundResource(R.drawable.search_airport_s_tab_bg)
                setTabFont(tab, typeface)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.setBackgroundResource(R.drawable.search_airport_uns_tab_bg)
                setTabFont(tab, typeface)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setTabFont(tab: TabLayout.Tab?, typeface: Typeface?) {
        val vgTab = tab?.view as? ViewGroup ?: return
        for (i in 0 until vgTab.childCount) {
            val tabViewChild = vgTab.getChildAt(i)
            if (tabViewChild is TextView) {
                tabViewChild.typeface = typeface
            }
        }
    }
}