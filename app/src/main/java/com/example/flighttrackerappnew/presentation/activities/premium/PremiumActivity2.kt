package com.example.flighttrackerappnew.presentation.activities.premium

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityPremium2Binding
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase2
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.DetailActivity
import com.example.flighttrackerappnew.presentation.activities.LiveMapFlightTrackerActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.LanguageActivity
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_2_LANGUAGE_SCREEN1
import com.example.flighttrackerappnew.presentation.google_play_billing.BillingEvent
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.PRIVACY_POLICY
import com.example.flighttrackerappnew.presentation.utils.TERM_OF_SERVICE
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.isFirstPremiumFlow
import com.example.flighttrackerappnew.presentation.utils.isFromDetail
import com.example.flighttrackerappnew.presentation.utils.openWebBrowser
import com.example.flighttrackerappnew.presentation.utils.setGradientText
import com.example.flighttrackerappnew.presentation.utils.setStyledSpan
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.visible
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PremiumActivity2 : BaseActivity<ActivityPremium2Binding>(ActivityPremium2Binding::inflate) {
    private val billingUseCase2: BillingUseCase2 by inject()
    private lateinit var countDownTimer: CountDownTimer
    private var pos = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnCross.visible()
        }, 2000)

        viewListener()
        billingEventListener()
        billingUseCase2.getProductDetails()
        onBackPress()
        setLayout()
        load()
    }

    private fun setLayout() {
        binding.apply {
            tvClaim.setStyledSpan(
                fullText = getString(R.string.claim_your_limited_time_discount),
                targetTexts = listOf("limited time"),
                targetStyle = R.style.sfb20p,
                defaultStyle = R.style.sfr20p,
            )
            discountPercent.setGradientText(
                "#F4A62C".toColorInt(),
                "#FFE658".toColorInt(),
                "#F4A62C".toColorInt()
            )
            tvAgree.setStyledSpan(
                getString(R.string.by_subscribing_you_agree_to_our_terms_of_use_and_privacy_policy),
                listOf("Terms of Use", "Privacy Policy"),
                R.style.sfr14s,
                R.style.sfr14s,
                underline = true,
                onClickListeners = listOf(
                    { openWebBrowser(TERM_OF_SERVICE) },
                    { openWebBrowser(PRIVACY_POLICY) }
                )
            )

            weeklyprice.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            weeklyprice.text = config.priceWeekly
            weeklyPriceSale.text = config.priceWeeklySale.toString()
            root.setPadding(
                0,
                getStatusBarHeight,
                0,
                0
            )
        }

        val remaining = config.getRemainingDiscountTime()

        if (remaining <= 0) {
            goToNormalPremium()
            return
        }

        startCountdown(remaining)
    }

    private fun onBackPress() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (intent.getBooleanExtra("from_splash", false)) {
                    startActivity(Intent(this@PremiumActivity2, LanguageActivity::class.java))
                } else {
                    finish()
                }
            }
        })
    }

    private fun billingEventListener() {
        lifecycleScope.launch {
            billingUseCase2.billingEvents.collect { event ->
                when (event) {
                    is BillingEvent.PurchaseSuccess -> {
                        config.isPremiumUser = true
                        showToast("Purchase success!")
                        if (intent.getBooleanExtra("from_liveLocked", false)) {
                            startActivity(
                                Intent(
                                    this@PremiumActivity2,
                                    LiveMapFlightTrackerActivity::class.java
                                )
                            )
                            finish()
                        } else if (intent.getBooleanExtra("from_arrival", false)) {
                            startActivity(
                                Intent(
                                    this@PremiumActivity2,
                                    DetailActivity::class.java
                                )
                            )
                            finish()
                        } else if (intent.getBooleanExtra("from_detail", false)) {
                            isFromDetail = true
                            startActivity(
                                Intent(
                                    this@PremiumActivity2,
                                    LiveMapFlightTrackerActivity::class.java
                                )
                            )
                            finish()
                        } else if (intent.getBooleanExtra("from_splash", false)) {
                            isFirstPremiumFlow = true
                            startActivity(
                                Intent(
                                    this@PremiumActivity2,
                                    LanguageActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            finish()
                        }
                    }

                    is BillingEvent.PurchaseAcknowledged -> {}

                    is BillingEvent.ProductDetailsLoaded -> {
                        if (event.loaded) {
                            binding.apply {
                                weeklyprice.apply {
                                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                }
                                weeklyprice.text = config.priceWeekly
                                weeklyPriceSale.text = config.priceWeeklySale.toString()
                            }
                        } else {
                            showToast("")
                        }
                    }

                    is BillingEvent.UserCancelled -> {
                        config.isPremiumUser = false
                        showToast("User cancelled")
                    }

                    is BillingEvent.Error -> {
                        showToast("Error: ${event.message}")
                    }

                    is BillingEvent.AlreadyOwned -> {}

                    is BillingEvent.BillingUnavailable -> {}

                    is BillingEvent.FeatureNotSupported -> {}

                    is BillingEvent.IsProductFound -> {}

                    is BillingEvent.NetworkError -> {}

                    is BillingEvent.QueryPurchaseResult -> {}

                    is BillingEvent.RestorePurchaseResult -> {
                        if (event.purchaseList.isNotEmpty()) {
                            this@PremiumActivity2.showToast("Your premium restore Successfully")
                            finish()
                        } else {
                            this@PremiumActivity2.showToast("You are not premium user")
                        }
                    }
                }
            }
        }
    }

    private fun load() {
        native_2_LANGUAGE_SCREEN1.loadNativeAd(
            this@PremiumActivity2,
            RemoteConfigManager.getBoolean("native_2_LANGUAGE_SCREEN1")
        )
    }

    private fun viewListener() {
        binding.apply {
            btnCross.setOnClickListener {
                if (intent.getBooleanExtra("from_splash", false)) {
                    startActivity(Intent(this@PremiumActivity2, LanguageActivity::class.java))
                } else {
                    finish()
                }
            }
            btnAvailDiscount.setOnClickListener {
                billingUseCase2.launchPurchases(
                    this@PremiumActivity2,
                    pos,
                )
            }
            txtPrivacy.setOnClickListener {
                openWebBrowser(PRIVACY_POLICY)
            }
            txtTerms.setOnClickListener {
                openWebBrowser(TERM_OF_SERVICE)
            }
            txtRestore.setOnClickListener {
                billingUseCase2.restorePurchase()
            }
        }
    }

    private fun startCountdown(timeRemaining: Long) {
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val days = millisUntilFinished / (1000 * 60 * 60 * 24)
                val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60

                binding.apply {
                    day.text = days.toString()
                    hour.text = hours.toString()
                    minute.text = minutes.toString()
                    second.text = seconds.toString()
                }
            }

            override fun onFinish() {
                goToNormalPremium()
            }
        }.start()
    }

    private fun goToNormalPremium() {
        val intent = Intent(this, PremiumActivity::class.java)
        intent.putExtra("fromSetting", false)
        intent.putExtra("from_splash", false)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) countDownTimer.cancel()
    }
}