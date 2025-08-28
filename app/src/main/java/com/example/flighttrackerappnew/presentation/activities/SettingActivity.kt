package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivitySettingBinding
import com.example.flighttrackerappnew.presentation.activities.beforeHome.LanguageActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.IS_FROM_SETTING_ACTIVITY
import com.example.flighttrackerappnew.presentation.utils.MORE_APPS
import com.example.flighttrackerappnew.presentation.utils.PRIVACY_POLICY
import com.example.flighttrackerappnew.presentation.utils.TERM_OF_SERVICE
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.openWebBrowser
import com.example.flighttrackerappnew.presentation.utils.rateApp
import com.example.flighttrackerappnew.presentation.utils.setZoomClickEffect
import com.example.flighttrackerappnew.presentation.utils.shareApp
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val NATIVE_SETTING =
            RemoteConfigManager.getBoolean("NATIVE_SETTING")

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        viewListener()

        if (NATIVE_SETTING && !config.isPremiumUser) {
            binding.flAdplaceholder.visible()
            nativeAdController.apply {
                loadNativeAd(
                    this@SettingActivity,
                    app.getString(R.string.NATIVE_SETTING)
                )
                showNativeAd(this@SettingActivity, binding.flAdplaceholder)
            }
        }
    }

    private fun viewListener() {
        binding.apply {
            btnBack.setZoomClickEffect()
            btnBack.setOnClickListener {
                finish()
            }
            tvSelectLanguage.setZoomClickEffect()
            tvSelectLanguage.setOnClickListener {
                IS_FROM_SETTING_ACTIVITY = true
                startActivity(Intent(this@SettingActivity, LanguageActivity::class.java))
            }
            tvMapStyle.setZoomClickEffect()
            tvMapStyle.setOnClickListener {
                IS_FROM_SETTING_ACTIVITY = true
                startActivity(Intent(this@SettingActivity, MapStyleActivity::class.java))
            }
            tvRateUs.setZoomClickEffect()
            tvRateUs.setOnClickListener {
                rateApp()
            }
            tvShare.setZoomClickEffect()
            tvShare.setOnClickListener {
                shareApp()
            }
            tvMore.setZoomClickEffect()
            tvMore.setOnClickListener {
                openWebBrowser(MORE_APPS)
            }
            tvPrivacy.setZoomClickEffect()
            tvPrivacy.setOnClickListener {
                openWebBrowser(PRIVACY_POLICY)
            }
            tvTermOfService.setZoomClickEffect()
            tvTermOfService.setOnClickListener {
                openWebBrowser(TERM_OF_SERVICE)
            }
        }
    }
}