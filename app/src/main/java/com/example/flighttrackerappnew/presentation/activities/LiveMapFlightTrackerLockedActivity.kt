package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.flighttrackerappnew.databinding.ActivityLiveMapFlightTrackerLockedBinding
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight

class LiveMapFlightTrackerLockedActivity : BaseActivity<ActivityLiveMapFlightTrackerLockedBinding>(
    ActivityLiveMapFlightTrackerLockedBinding::inflate
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.root.setPadding(
            0,
            getStatusBarHeight,
            0,
            0
        )
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.positiveBtn.setOnClickListener {
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