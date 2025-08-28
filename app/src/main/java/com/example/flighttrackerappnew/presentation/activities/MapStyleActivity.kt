package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.databinding.ActivityMapStyleBinding
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.enums.MapOptionSelected
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.IS_FROM_SETTING_ACTIVITY
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.visible
import com.google.android.gms.maps.GoogleMap
import org.koin.android.ext.android.inject

class MapStyleActivity : BaseActivity<ActivityMapStyleBinding>(ActivityMapStyleBinding::inflate) {

    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val NATIVE_MAP =
            RemoteConfigManager.getBoolean("NATIVE_MAP")

        if (NATIVE_MAP && !config.isPremiumUser) {
            nativeAdController.showMapStyleNativeAd(this, binding.flAdplaceholder)
            binding.flAdplaceholder.visible()
        }
        val params = binding.tvTitle.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.tvTitle.layoutParams = params

        viewListeners()
    }

    private fun viewListeners() {
        binding.apply {
//            btnBack.setOnClickListener {
//                finish()
//            }
            selectBtn.setOnClickListener {
                if (IS_FROM_SETTING_ACTIVITY) {
                    IS_FROM_SETTING_ACTIVITY = false
                    finish()
                } else {
                    val intent = Intent(this@MapStyleActivity, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
            cons1.setOnClickListener {
                binding.selectBtn.visible()
                config.mapStyle = GoogleMap.MAP_TYPE_NORMAL
                updateMapSelection(MapOptionSelected.SELECTED_OPTION1)
            }
            con2.setOnClickListener {
                binding.selectBtn.visible()
                config.mapStyle = GoogleMap.MAP_TYPE_HYBRID
                updateMapSelection(MapOptionSelected.SELECTED_OPTION2)
            }
        }
    }

    private fun updateMapSelection(option: MapOptionSelected) {
        binding.apply {
            cons1.background = ContextCompat.getDrawable(
                this@MapStyleActivity,
                option.option1
            )
            con2.background = ContextCompat.getDrawable(
                this@MapStyleActivity,
                option.option2
            )
        }
    }
}