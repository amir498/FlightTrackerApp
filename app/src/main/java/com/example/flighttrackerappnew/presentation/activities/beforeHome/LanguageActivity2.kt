package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.LanguageDataList
import com.example.flighttrackerappnew.databinding.ActivityLanguage2Binding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.main.MainActivity
import com.example.flighttrackerappnew.presentation.adapter.LanguageActivityAdapter
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_2_LANGUAGE_SCREEN2
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.IS_FROM_SETTING_ACTIVITY
import com.example.flighttrackerappnew.presentation.utils.setZoomClickEffect
import com.example.flighttrackerappnew.presentation.utils.visible

class LanguageActivity2 :
    BaseActivity<ActivityLanguage2Binding>(ActivityLanguage2Binding::inflate) {

    private lateinit var adapter: LanguageActivityAdapter
    private var firstClicked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = LanguageActivityAdapter(config) {
            binding.btnSelect.visible()
        }
        setRecyclerView()
        viewListener()

        showAd()
    }

    private fun showAd() {
        NativeAdProvider.apply {
            native_1_LANGUAGE_SCREEN2.showNativeAd(
                adGroup = native_1_LANGUAGE_SCREEN2,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_layout_view_with_media,
               activity =  this@LanguageActivity2
            )

        }
    }

    override fun onResume() {
        super.onResume()
        native_2_LANGUAGE_SCREEN2.loadNativeAd(
            this@LanguageActivity2,
            RemoteConfigManager.getBoolean("native_2_LANGUAGE_SCREEN2")
        )
    }

    private fun viewListener() {
        binding.apply {
            btnSelect.setZoomClickEffect()
            btnSelect.setOnClickListener {
                if (IS_FROM_SETTING_ACTIVITY) {
                    IS_FROM_SETTING_ACTIVITY = false
                    startActivity(Intent(this@LanguageActivity2, MainActivity::class.java))
                    finishAffinity()
                } else {
                    InterstitialAdManager.showAd(
                        interstitialLoadingScreenShowTime = 0L,
                        showLoadingScreenAsLoadAdRequestCalled = false,
                        this@LanguageActivity2,
                        showLoadingScreenWithDelay = 0,
                        ) {
                        startActivity(Intent(this@LanguageActivity2, OnBoardingActivity::class.java))
                    }
                }
            }
        }
    }

    private fun setRecyclerView() {
        binding.recyclerview.adapter = adapter
        adapter.setDataList(getLanguageData())
        adapter.setListener {
            if (firstClicked) {
                if (!config.isPremiumUser) {
                    NativeAdProvider.apply {
                        native_2_LANGUAGE_SCREEN2.showNativeAd(
                            showFakeLoading = true,
                            adGroup = native_2_LANGUAGE_SCREEN2,
                            frameLayout = binding.flAdplaceholder,
                            adLayout = R.layout.native_ad_layout_view_with_media,
                          activity =   this@LanguageActivity2
                        )
                    }
                }
                firstClicked = false
            }
        }
    }

    private fun getLanguageData(): ArrayList<LanguageDataList> =
        arrayListOf<LanguageDataList>().apply {
            add(
                LanguageDataList(
                    R.drawable.iv_aus,
                    ContextCompat.getString(this@LanguageActivity2, R.string.tv_australia),
                    "en-AU",
                    type = 2
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_uk,
                    ContextCompat.getString(this@LanguageActivity2, R.string.tv_england),
                    "en-GB",
                    type = 2
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_us,
                    ContextCompat.getString(this@LanguageActivity2, R.string.tv_america),
                    "en-US",
                    type = 2
                )
            )

            add(
                LanguageDataList(
                    R.drawable.iv_ireland,
                    ContextCompat.getString(this@LanguageActivity2, R.string.tv_ireland),
                    "en-IE",
                    type = 2
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_newzeland,
                    ContextCompat.getString(this@LanguageActivity2, R.string.tv_newzeland),
                    "en-NZ",
                    type = 2
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_hindi,
                    ContextCompat.getString(this@LanguageActivity2, R.string.tvIndia),
                    "en-IN",
                    type = 2
                )
            )
        }
}