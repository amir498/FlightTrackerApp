package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivitySettingBinding
import com.example.flighttrackerappnew.presentation.activities.beforeHome.LanguageActivity
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_SETTING
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.IS_FROM_SETTING_ACTIVITY
import com.example.flighttrackerappnew.presentation.utils.MORE_APPS
import com.example.flighttrackerappnew.presentation.utils.PRIVACY_POLICY
import com.example.flighttrackerappnew.presentation.utils.TERM_OF_SERVICE
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.openWebBrowser
import com.example.flighttrackerappnew.presentation.utils.rateApp
import com.example.flighttrackerappnew.presentation.utils.shareApp

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        viewListener()
        loadAd()
    }

    private fun loadAd() {
        NATIVE_SETTING.apply {
            loadNativeAd(
                this@SettingActivity,
                RemoteConfigManager.getBoolean("NATIVE_SETTING")
            )
            showNativeAd(
                adGroup = NATIVE_SETTING,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_layout_view_with_media,
               activity =  this@SettingActivity
            )
        }
    }

    private fun viewListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            tvSelectLanguage.setOnClickListener {
                IS_FROM_SETTING_ACTIVITY = true
                startActivity(Intent(this@SettingActivity, LanguageActivity::class.java))
            }
            tvMapStyle.setOnClickListener {
                IS_FROM_SETTING_ACTIVITY = true
                startActivity(Intent(this@SettingActivity, MapStyleActivity::class.java))
            }
            tvRateUs.setOnClickListener {
                rateApp()
            }
            tvShare.setOnClickListener {
                shareApp()
            }
            tvMore.setOnClickListener {
                openWebBrowser(MORE_APPS)
            }
            tvPrivacy.setOnClickListener {
                openWebBrowser(PRIVACY_POLICY)
            }
            tvTermOfService.setOnClickListener {
                openWebBrowser(TERM_OF_SERVICE)
            }
        }
    }
}