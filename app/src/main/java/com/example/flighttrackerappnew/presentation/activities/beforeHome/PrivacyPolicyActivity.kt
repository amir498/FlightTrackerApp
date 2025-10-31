package com.example.flighttrackerappnew.presentation.activities.beforeHome

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityPrivacyPolicyBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2
import com.example.flighttrackerappnew.presentation.admob.interstitial.InterstitialAdManager
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.openWebBrowser
import com.example.flighttrackerappnew.presentation.utils.showToast

class PrivacyPolicyActivity :
    BaseActivity<ActivityPrivacyPolicyBinding>(ActivityPrivacyPolicyBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.root.setPadding(
            0,
            getStatusBarHeight,
            0,
            0
        )
        viewListener()

        makeString()
        loadAd()
    }

    private fun loadAd() {
        NativeAdProvider.native_2_LANGUAGE_SCREEN1.loadNativeAd(
            this,
            RemoteConfigManager.getBoolean("native_2_LANGUAGE_SCREEN1")
        )
    }

    private fun makeString() {
        val fullText = "I have read and agree to the Privacy Policy"
        val spannable = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                openWebBrowser("https://sites.google.com/view/tanydev-flight-tracker")
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = ContextCompat.getColor(this@PrivacyPolicyActivity, R.color.acc1)
            }
        }

        val start = fullText.indexOf("Privacy Policy")
        val end = start + "Privacy Policy".length

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.apply {
            tvv2.text = spannable
            tvv2.movementMethod = LinkMovementMethod.getInstance()
            tvv2.highlightColor = Color.TRANSPARENT
        }
    }

    private fun viewListener() {
        binding.apply {
            ivPrivacyCheck.setOnClickListener {
                if (ivPrivacyCheck.contentDescription == "no") {
                    ivPrivacyCheck.contentDescription = "yes"
                    ivPrivacyCheck.setImageResource(R.drawable.iv_privacy_check_true)
                } else {
                    ivPrivacyCheck.contentDescription = "no"
                    ivPrivacyCheck.setImageResource(R.drawable.iv_privacy_check)
                }
            }
            AcceptBtn.setOnClickListener {
                if (ivPrivacyCheck.contentDescription == "yes") {
                    config.isPrivacyPolicyAccepted = true
                    if (!config.isPremiumUser) {
                        InterstitialAdManager.showAd(
                            interstitialLoadingScreenShowTime = 0L,
                            showLoadingScreenAsLoadAdRequestCalled = false,
                            this@PrivacyPolicyActivity,
                            showLoadingScreenWithDelay = 0,
                        ) {
                            showPremiumScreen()
                            finish()
                        }
                    } else {
                        InterstitialAdManager.showAd(
                            interstitialLoadingScreenShowTime = 0L,
                            showLoadingScreenAsLoadAdRequestCalled = false,
                            this@PrivacyPolicyActivity,
                            showLoadingScreenWithDelay = 0,
                        ) {
                            startActivity(
                                Intent(
                                    this@PrivacyPolicyActivity,
                                    LanguageActivity::class.java
                                )
                            )
                            finish()
                        }
                    }
                } else {
                    this@PrivacyPolicyActivity.showToast("Please agree to the Privacy Policy before continuing")
                }
            }
            DeclineBtn.setOnClickListener {
                config.isPrivacyPolicyAccepted = false
                finish()
            }
        }
    }

    private fun showPremiumScreen() {
        config.startDiscountIfNeeded()

        val isDiscountActive = config.isDiscountActive()

        if (isDiscountActive) {
            val intent = Intent(this@PrivacyPolicyActivity, PremiumActivity2::class.java)
            intent.putExtra("from_splash", true)
            startActivity(intent)
        } else {
            val intent = Intent(this@PrivacyPolicyActivity, PremiumActivity::class.java)
            intent.putExtra("from_splash", true)
            startActivity(intent)
        }
    }
}