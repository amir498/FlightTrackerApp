package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.LanguageDataList
import com.example.flighttrackerappnew.databinding.ActivityLanguage2Binding
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

class LanguageActivity2 :
    BaseActivity<ActivityLanguage2Binding>(ActivityLanguage2Binding::inflate) {

    private lateinit var adapter: LanguageActivityAdapter
    private var firstClicked = true
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
        val showNative2Lang2 =
            RemoteConfigManager.getBoolean("NATIVE2_LANGUAGESCREEN2")
        val showNative1Lang2 =
            RemoteConfigManager.getBoolean("NATIVE1_LANGUAGESCREEN2")

        nativeAdController.apply {
            if (showNative2Lang2 && !config.isPremiumUser) {
                loadLanguageScreen2NativeAd2(
                    this@LanguageActivity2,
                    app.getString(R.string.NATIVE2_LANGUAGESCREEN2)
                )
            }
        }

        if (showNative1Lang2) {
            if (!config.isPremiumUser) {
                nativeAdController.showLanguageScreen2NativeAd1(
                    this@LanguageActivity2,
                    binding.flAdplaceholder
                )
            }
        }

        if ((showNative1Lang2 || showNative2Lang2) && !config.isPremiumUser) {
            binding.flAdplaceholder.visible()
        } else {
            binding.flAdplaceholder.gone()
        }
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
                    startActivity(Intent(this@LanguageActivity2, OnBoardingActivity::class.java))
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
                    nativeAdController.showLanguageScreen2NativeAd2(this, binding.flAdplaceholder)
                    val showOB1 =
                        RemoteConfigManager.getBoolean("NATIVE_ONB1")
                    if (showOB1) {
                        nativeAdController.loadOnb1NativeAd(
                            this,
                            app.getString(R.string.NATIVE_ONB1)
                        )
                    }
                }
            }
            firstClicked = false
        }
    }

    private fun getLanguageData(): ArrayList<LanguageDataList> =
        arrayListOf<LanguageDataList>().apply {
            add(
                LanguageDataList(
                    R.drawable.iv_aus,
                    ContextCompat.getString(this@LanguageActivity2, R.string.tv_newzeland),
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
                    ContextCompat.getString(this@LanguageActivity2, R.string.tvFrench),
                    "en-NZ",
                    type = 2
                )
            )
        }
}