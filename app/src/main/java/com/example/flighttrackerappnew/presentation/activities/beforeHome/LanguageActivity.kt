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
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adapter.LanguageActivityAdapter
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.IS_FROM_SETTING_ACTIVITY
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.setZoomClickEffect
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    private lateinit var adapter: LanguageActivityAdapter

    private val nativeAdController: NativeAdController by inject()

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

        val showNative2Lang1 =
            RemoteConfigManager.getBoolean("NATIVE2_LANGUAGESCREEN1")
        val showNative1Lang1 =
            RemoteConfigManager.getBoolean("NATIVE1_LANGUAGESCREEN1")
        val showOBFull1 =
            RemoteConfigManager.getBoolean("NATIVE_ONB_Full1")

        if (showNative2Lang1) {
            nativeAdController.apply {
                loadLanguageScreen1NativeAd2(
                    this@LanguageActivity,
                    app.getString(R.string.NATIVE2_LANGUAGESCREEN1)
                )
            }
        }

        if (showOBFull1) {
            nativeAdController.loadFullNativeAd1(
                this,
                app.getString(R.string.NATIVE_ONB_Full1)
            )
        }

        if (showNative1Lang1) {
            binding.flAdplaceholder.visible()
            nativeAdController.showLanguageScreen1NativeAd1(
                this@LanguageActivity,
                binding.flAdplaceholder,
            )
        }

        if (showNative1Lang1 || showNative2Lang1) {
            binding.flAdplaceholder.visible()
        } else {
            binding.flAdplaceholder.gone()
        }
    }

    private fun viewListener() {
        binding.apply {
            btnSelect.setZoomClickEffect()
            btnSelect.setOnClickListener {
                if (config.selectedLanguageCode == "en") {
                    startActivity(Intent(this@LanguageActivity, LanguageActivity2::class.java))
                } else {
                    if (IS_FROM_SETTING_ACTIVITY) {
                        IS_FROM_SETTING_ACTIVITY = false
                        startActivity(Intent(this@LanguageActivity, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        startActivity(Intent(this@LanguageActivity, OnBoardingActivity::class.java))
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
            if (firstClicked) {
                nativeAdController.showLanguageScreen1NativeAd2(this, binding.flAdplaceholder)
                val showNative1Lang2 =
                    RemoteConfigManager.getBoolean("NATIVE1_LANGUAGESCREEN2")
                if (showNative1Lang2) {
                    nativeAdController.loadLanguageScreen2NativeAd1(
                        this,
                        app.getString(R.string.NATIVE1_LANGUAGESCREEN2)
                    )
                }

            }
            firstClicked = false
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
                    R.drawable.iv_eng,
                    ContextCompat.getString(this@LanguageActivity, R.string.tvEnglish),
                    "en",
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
        }
}