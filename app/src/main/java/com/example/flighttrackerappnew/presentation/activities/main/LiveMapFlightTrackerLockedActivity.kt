package com.example.flighttrackerappnew.presentation.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.flighttrackerappnew.databinding.ActivityLiveMapFlightTrackerLockedBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2

class LiveMapFlightTrackerLockedActivity : BaseActivity<ActivityLiveMapFlightTrackerLockedBinding>(
    ActivityLiveMapFlightTrackerLockedBinding::inflate
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewListener()
    }

    private fun viewListener(){
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.positiveBtn.setOnClickListener {
            showPremiumScreen()
        }
    }

    private fun showPremiumScreen() {
        config.startDiscountIfNeeded()

        val isDiscountActive = config.isDiscountActive()

        if (isDiscountActive) {
            val intent = Intent(this, PremiumActivity2::class.java)
            intent.putExtra("from_liveLocked", true)
            startActivity(intent)
        } else {
            val intent = Intent(this, PremiumActivity::class.java)
            intent.putExtra("from_liveLocked", true)
            startActivity(intent)
        }
    }

    private fun onBackPress() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        onBackPress()
    }
}