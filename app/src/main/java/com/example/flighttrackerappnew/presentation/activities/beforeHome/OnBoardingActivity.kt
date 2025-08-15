package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flighttrackerappnew.databinding.ActivityOnBoardingBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.adapter.OnBoardingPagerAdapter
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight

class OnBoardingActivity :
    BaseActivity<ActivityOnBoardingBinding>(ActivityOnBoardingBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.viewPager.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.viewPager.layoutParams = params

        val adapter = OnBoardingPagerAdapter(this)
        binding.viewPager.adapter = adapter
    }
}