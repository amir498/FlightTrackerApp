package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.LanguageDataList
import com.example.flighttrackerappnew.databinding.ActivityLanguageBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.MainActivity
import com.example.flighttrackerappnew.presentation.adapter.LanguageActivityAdapter
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_1_LANGUAGE_SCREEN1
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_1_LANGUAGE_SCREEN2
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_OnBoarding1
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.IS_FROM_SETTING_ACTIVITY
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.isFirstPremiumFlow
import com.example.flighttrackerappnew.presentation.utils.setZoomClickEffect
import com.example.flighttrackerappnew.presentation.utils.visible

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    private lateinit var adapter: LanguageActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = LanguageActivityAdapter(config) {
            binding.btnSelect.visible()
        }
        setRecyclerView()
        viewListener()

        val params = binding.chooseLanguage.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.chooseLanguage.layoutParams = params

        showAd()
    }

    private fun showAd() {
        native_1_LANGUAGE_SCREEN1.showNativeAd(
            adGroup = native_1_LANGUAGE_SCREEN1,
            frameLayout = binding.flAdplaceholder,
            adLayout = R.layout.native_ad_layout_view_with_media,
            activity = this@LanguageActivity
        )
    }

    override fun onResume() {
        super.onResume()
        native_OnBoarding1.loadNativeAd(
            this@LanguageActivity,
            RemoteConfigManager.getBoolean("native_OnBoarding1")
        )
        native_1_LANGUAGE_SCREEN2.loadNativeAd(
            this@LanguageActivity,
            RemoteConfigManager.getBoolean("native_1_LANGUAGE_SCREEN2")
        )
    }

    private fun viewListener() {
        binding.apply {
            btnSelect.setZoomClickEffect()
            btnSelect.setOnClickListener {
                if (!config.isPremiumUser) {
                    if (config.selectedLanguageCode == "en") {
                        startActivity(Intent(this@LanguageActivity, LanguageActivity2::class.java))
                    } else {
                        if (IS_FROM_SETTING_ACTIVITY) {
                            IS_FROM_SETTING_ACTIVITY = false
                            startActivity(Intent(this@LanguageActivity, MainActivity::class.java))
                            finishAffinity()
                        } else {
                            InterstitialAdManager.showAd(
                                interstitialLoadingScreenShowTime = 0L,
                                showLoadingScreenAsLoadAdRequestCalled = false,
                                this@LanguageActivity,
                                showLoadingScreenWithDelay = 0,
                                ) {
                                startActivity(
                                    Intent(
                                        this@LanguageActivity,
                                        OnBoardingActivity::class.java
                                    )
                                )
                            }
                        }
                    }
                } else {
                    if (isFirstPremiumFlow) {
                        isFirstPremiumFlow = false
                        startActivity(
                            Intent(
                                this@LanguageActivity,
                                OnBoardingActivity::class.java
                            )
                        )
                    } else {
                        IS_FROM_SETTING_ACTIVITY = false
                        startActivity(Intent(this@LanguageActivity, MainActivity::class.java))
                        finishAffinity()
                    }
                }
            }
        }
    }

    private var firstClicked = true
    private fun setRecyclerView() {
        binding.recyclerview.adapter = adapter
        adapter.setDataList(getLanguageData())
        adapter.setListener {
            if (!config.isPremiumUser) {
                if (firstClicked) {
                    NativeAdProvider.apply {
                        native_2_LANGUAGE_SCREEN1.showNativeAd(
                            showFakeLoading = true,
                            adGroup = native_2_LANGUAGE_SCREEN1,
                            frameLayout = binding.flAdplaceholder,
                            adLayout = R.layout.native_ad_layout_view_with_media,
                           activity =  this@LanguageActivity
                        )
                            native_OnBoarding3.loadNativeAd(
                                this@LanguageActivity,
                                RemoteConfigManager.getBoolean("native_OnBoarding3")
                            )
                        }
                    firstClicked = false
                }
            }
        }
    }

    private fun getLanguageData(): ArrayList<LanguageDataList> =
        arrayListOf<LanguageDataList>().apply {
            add(
                LanguageDataList(
                    R.drawable.iv_vietnamese,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvVietnamese),
                    "vi",
                    type = 1
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_pakistan,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvUrdu),
                    "ur",
                    type = 1
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_china,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvChinese),
                    "zh",
                    type = 1
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_french,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvFrench),
                    "fr",
                    type = 1
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_hindi,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvHindi),
                    "hi",
                    type = 1
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_indonesia,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvIndonesia),
                    "id",
                    type = 1
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_saudi_arabia,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvArabic),
                    "ar",
                    type = 1
                )
            )
            add(
                LanguageDataList(
                    R.drawable.iv_eng,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvEnglish),
                    "en",
                    type = 1
                )
            )
        }
}