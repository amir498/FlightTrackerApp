package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityMapStyleBinding
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_MAP
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_MAP2
import com.example.flighttrackerappnew.presentation.enums.MapOptionSelected
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.IS_FROM_SETTING_ACTIVITY
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.visible
import com.google.android.gms.maps.GoogleMap

class MapStyleActivity : BaseActivity<ActivityMapStyleBinding>(ActivityMapStyleBinding::inflate) {
    private var firstClick: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.tvTitle.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.tvTitle.layoutParams = params

        viewListeners()
        showAd()
    }

    private fun showAd() {
        NATIVE_MAP.showNativeAd(
            adGroup = NATIVE_MAP,
            frameLayout = binding.flAdplaceholder,
            adLayout = R.layout.native_ad_layout_view_with_media,
           activity =  this@MapStyleActivity as AppCompatActivity
        )
    }

    private fun viewListeners() {
        binding.apply {
            selectBtn.setOnClickListener {
                if (IS_FROM_SETTING_ACTIVITY) {
                    IS_FROM_SETTING_ACTIVITY = false
                    finish()
                } else {
                    InterstitialAdManager.loadInterstitialAd(
                        ignoreClickCount = true,
                        showLoadingScreenWithDelay = 0L,
                        showLoadingAsLoadAdRequestCalled = true,
                        interstitialLoadingScreenShowTime = RemoteConfigManager.getNumber("Interstitial_loading_screen_show_time"),
                        showWhenReady = true,
                        activity = this@MapStyleActivity,
                        adUnitId = app.getString(R.string.INTERSTITIAL_MAP_STYLE),
                        isInterstitialEnabled = RemoteConfigManager.getBoolean("INTERSTITIAL_MAP_STYLE"),
                        adLoadingTimeOut = RemoteConfigManager.getNumber("Interstitial_time_out"),
                        {
                            val intent = Intent(this@MapStyleActivity, MainActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        }, {
                        })
                }
            }
            cons1.setOnClickListener {
                if (firstClick) {
                    showDuplicatedAd()
                }
                binding.selectBtn.visible()
                config.mapStyle = GoogleMap.MAP_TYPE_NORMAL
                updateMapSelection(MapOptionSelected.SELECTED_OPTION1)
            }
            con2.setOnClickListener {
                if (firstClick) {
                    showDuplicatedAd()
                }
                binding.selectBtn.visible()
                config.mapStyle = GoogleMap.MAP_TYPE_HYBRID
                updateMapSelection(MapOptionSelected.SELECTED_OPTION2)
            }
        }
    }

    fun showDuplicatedAd() {
        NATIVE_MAP2.showNativeAd(
            showFakeLoading = true,
            adGroup = NATIVE_MAP2,
            frameLayout = binding.flAdplaceholder,
            adLayout = R.layout.native_ad_layout_view_with_media,
            activity = this@MapStyleActivity as AppCompatActivity
        )
        firstClick = false
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