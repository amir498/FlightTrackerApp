package com.example.flighttrackerappnew.presentation.activities.premium

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityPremiumBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(ActivityPremiumBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewListener()
        binding.root.setPadding(
            0,
            getStatusBarHeight,
            0,
            0
        )
    }

    private fun viewListener() {
        binding.apply {
            btnClosePremium.setOnClickListener {
                finish()
            }
            btnUpgradeNow.setOnClickListener {

            }
            conWeeklyPlan.setOnClickListener {
                conWeeklyPlan.background = ContextCompat.getDrawable(this@PremiumActivity,R.drawable.weekly_plan_bg)
                conYearlyPlan.background = ContextCompat.getDrawable(this@PremiumActivity,R.drawable.yearly_plan_bg)
            }
            conYearlyPlan.setOnClickListener {
                conYearlyPlan.background = ContextCompat.getDrawable(this@PremiumActivity,R.drawable.weekly_plan_bg)
                conWeeklyPlan.background = ContextCompat.getDrawable(this@PremiumActivity,R.drawable.yearly_plan_bg)
            }
        }
    }
}