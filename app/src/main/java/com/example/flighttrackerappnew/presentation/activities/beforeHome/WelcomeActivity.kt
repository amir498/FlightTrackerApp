package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityWelcomeBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.MapStyleActivity
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_MAP2
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_WELCOME
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_WELCOME2
import com.example.flighttrackerappnew.presentation.enums.WelcomeOptionSelected
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.visible

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(ActivityWelcomeBinding::inflate) {
    private var firstClick = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.ivWelcome.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.ivWelcome.layoutParams = params

        viewListeners()
        showAd()
        loadAd()
    }

    private fun loadAd() {
        NATIVE_MAP2.loadNativeAd(
            this,
            RemoteConfigManager.getBoolean("NATIVE_MAP2")
        )
    }

    private fun showAd() {
        NATIVE_WELCOME.showNativeAd(
            adGroup = NATIVE_WELCOME,
            frameLayout = binding.flAdplaceholder,
            adLayout = R.layout.native_ad_layout_view_with_media,
           activity =  this@WelcomeActivity as AppCompatActivity
        )
    }

    private fun viewListeners() {
        binding.apply {
            ivTickWelcome.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, MapStyleActivity::class.java))
            }
            tick.setOnClickListener {
                if (firstClick) {
                    showDuplicateAd()
                }
                binding.ivTickWelcome.apply {
                    if (isInvisible) visible()
                }
                updateWelcomeSelection(WelcomeOptionSelected.SELECTED_OPTION1)
            }
            tick2.setOnClickListener {
                if (firstClick) {
                    showDuplicateAd()
                }
                binding.ivTickWelcome.apply {
                    if (isInvisible) visible()
                }
                updateWelcomeSelection(WelcomeOptionSelected.SELECTED_OPTION2)
            }
            tick3.setOnClickListener {
                if (firstClick) {
                    showDuplicateAd()
                }
                binding.ivTickWelcome.apply {
                    if (isInvisible) visible()
                }
                updateWelcomeSelection(WelcomeOptionSelected.SELECTED_OPTION3)
            }
        }
    }

    private fun showDuplicateAd() {
        firstClick = false
        NATIVE_WELCOME2.showNativeAd(
            showFakeLoading = true,
            adGroup = NATIVE_WELCOME2,
            frameLayout = binding.flAdplaceholder,
            adLayout = R.layout.native_ad_layout_view_with_media,
           activity =  this@WelcomeActivity as AppCompatActivity
        )
    }

    private fun updateWelcomeSelection(option: WelcomeOptionSelected) {
        binding.apply {
            tick.setImageDrawable(
                ContextCompat.getDrawable(
                    this@WelcomeActivity,
                    option.option1
                )
            )
            tick2.setImageDrawable(
                ContextCompat.getDrawable(
                    this@WelcomeActivity,
                    option.option2
                )
            )
            tick3.setImageDrawable(
                ContextCompat.getDrawable(
                    this@WelcomeActivity,
                    option.option3
                )
            )
        }
    }
}