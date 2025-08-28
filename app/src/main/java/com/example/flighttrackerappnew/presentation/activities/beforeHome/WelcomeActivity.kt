package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityWelcomeBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.MapStyleActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.enums.WelcomeOptionSelected
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(ActivityWelcomeBinding::inflate) {

    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val NATIVE_MAP =
            RemoteConfigManager.getBoolean("NATIVE_MAP")
        val NATIVE_WELCOME =
            RemoteConfigManager.getBoolean("NATIVE_WELCOME")

        val params = binding.ivWelcome.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.ivWelcome.layoutParams = params

        viewListeners()

        if (NATIVE_MAP && !config.isPremiumUser){
            nativeAdController.apply {
                loadMapStyleNativeAd(
                    this@WelcomeActivity,
                    app.getString(R.string.NATIVE_MAP)
                )
            }
        }

        if (NATIVE_WELCOME && !config.isPremiumUser) {
            binding.flAdplaceholder.visible()
            nativeAdController.showWelcomeScreenNativeAd(
                this@WelcomeActivity,
                binding.flAdplaceholder
            )
        }
    }

    private fun viewListeners() {
        binding.apply {
            ivTickWelcome.setOnClickListener {
                startActivity(Intent(this@WelcomeActivity, MapStyleActivity::class.java))
            }
            tick.setOnClickListener {
                binding.ivTickWelcome.apply {
                    if (isInvisible) visible()
                }
                updateWelcomeSelection(WelcomeOptionSelected.SELECTED_OPTION1)
            }
            tick2.setOnClickListener {
                binding.ivTickWelcome.apply {
                    if (isInvisible) visible()
                }
                updateWelcomeSelection(WelcomeOptionSelected.SELECTED_OPTION2)
            }
            tick3.setOnClickListener {
                binding.ivTickWelcome.apply {
                    if (isInvisible) visible()
                }
                updateWelcomeSelection(WelcomeOptionSelected.SELECTED_OPTION3)
            }
        }
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