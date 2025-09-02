package com.example.flighttrackerappnew.presentation.activities.premium

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.ActivityPremiumBinding
import com.example.flighttrackerappnew.domain.usecase.BillingUseCase
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.DetailActivity
import com.example.flighttrackerappnew.presentation.activities.LiveMapFlightTrackerActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.LanguageActivity
import com.example.flighttrackerappnew.presentation.google_play_billing.BillingEvent
import com.example.flighttrackerappnew.presentation.utils.PRIVACY_POLICY
import com.example.flighttrackerappnew.presentation.utils.TERM_OF_SERVICE
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.isFirstPremiumFlow
import com.example.flighttrackerappnew.presentation.utils.isFromDetail
import com.example.flighttrackerappnew.presentation.utils.loadAppOpen
import com.example.flighttrackerappnew.presentation.utils.openWebBrowser
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.visible
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(ActivityPremiumBinding::inflate) {
    private val billingUseCase: BillingUseCase by inject()
    private var pos = 1
    private var hasFreeTrailEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnClosePremium.visible()
        }, 2000)
        binding.apply {
            weeklyPrice.text = config.priceWeekly.toString()
            yearlyPrice.text = config.priceYearly.toString()
            root.setPadding(
                0,
                getStatusBarHeight,
                0,
                0
            )
        }

        viewListener()
        billingEventListener()
        billingUseCase.getProductDetails()
        onBackPress()
        if (config.isFreeTrailAvailable) {
            binding.conFreeTrail.visible()
            hasFreeTrailEnabled = true
        } else {
            binding.conFreeTrail.gone()
            hasFreeTrailEnabled = false
            binding.btnUpgradeNow.text = getString(R.string.subscribe_now)
        }
    }

    private fun onBackPress() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (intent.getBooleanExtra("from_splash", false)) {
                    startActivity(Intent(this@PremiumActivity, LanguageActivity::class.java))
                } else {
                    finish()
                }
            }
        })
    }

    private fun billingEventListener() {
        lifecycleScope.launch {
            billingUseCase.billingEvents.collect { event ->
                when (event) {
                    is BillingEvent.PurchaseSuccess -> {
                        config.isPremiumUser = true
                        showToast("Purchase success!")
                        if (intent.getBooleanExtra("from_liveLocked", false)) {
                            startActivity(
                                Intent(
                                    this@PremiumActivity,
                                    LiveMapFlightTrackerActivity::class.java
                                )
                            )
                            finish()
                        } else if (intent.getBooleanExtra("from_arrival", false)) {
                            startActivity(
                                Intent(
                                    this@PremiumActivity,
                                    DetailActivity::class.java
                                )
                            )
                            finish()
                        } else if (intent.getBooleanExtra("from_detail", false)) {
                            isFromDetail = true
                            startActivity(
                                Intent(
                                    this@PremiumActivity,
                                    LiveMapFlightTrackerActivity::class.java
                                )
                            )
                            finish()
                        } else if (intent.getBooleanExtra("from_splash", false)) {
                            isFirstPremiumFlow = true
                            startActivity(
                                Intent(
                                    this@PremiumActivity,
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
                                weeklyPrice.text = config.priceWeekly.toString()
                                yearlyPrice.text = config.priceYearly.toString()
                                savePercentage.text = config.savePercent
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

                    is BillingEvent.QueryPurchaseResult -> {
                    }

                    is BillingEvent.RestorePurchaseResult -> {
                        if (event.purchaseList.isNotEmpty()) {
                            this@PremiumActivity.showToast("Your premium restore Successfully")
                            finish()
                        } else {
                            this@PremiumActivity.showToast("You are not premium user")
                        }
                    }
                }
            }
        }
    }

    private fun viewListener() {
        binding.apply {
            txtPrivacy.setOnClickListener {
                openWebBrowser(PRIVACY_POLICY)
            }
            txtTerms.setOnClickListener {
                openWebBrowser(TERM_OF_SERVICE)
            }
            txtRestore.setOnClickListener {
                billingUseCase.restorePurchase()
            }
            btnClosePremium.setOnClickListener {
                if (intent.getBooleanExtra("from_splash", false)) {
                    startActivity(Intent(this@PremiumActivity, LanguageActivity::class.java))
                } else {
                    finish()
                }
            }
            btnUpgradeNow.setOnClickListener {
                billingUseCase.launchPurchases(
                    this@PremiumActivity,
                    pos,
                    hasFreeTrailEnabled,
                )
            }
            conWeeklyPlan.setOnClickListener {
                setWeeklyClickedUi()
            }
            conYearlyPlan.setOnClickListener {
                setYearlyClickedUi()
            }
            hbtnSwitchCompact.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    hasFreeTrailEnabled = true
                    pos = 1
                    conYearlyPlan.background =
                        ContextCompat.getDrawable(this@PremiumActivity, R.drawable.yearly_plan_bg_s)
                    conWeeklyPlan.background =
                        ContextCompat.getDrawable(this@PremiumActivity, R.drawable.yearly_plan_bg)

                    hbtnSwitchCompactCons.background = ContextCompat.getDrawable(
                        this@PremiumActivity,
                        R.drawable.free_trail_switch_bg_s
                    )
                    btnUpgradeNow.text =
                        ContextCompat.getString(this@PremiumActivity, R.string.start_free_trail)
                } else {
                    hasFreeTrailEnabled = false
                    hbtnSwitchCompactCons.background = ContextCompat.getDrawable(
                        this@PremiumActivity,
                        R.drawable.free_trail_switch_bg
                    )
                    btnUpgradeNow.text =
                        ContextCompat.getString(this@PremiumActivity, R.string.subscribe_now)
                }
            }
        }
    }

    private fun setWeeklyClickedUi() {
        binding.apply {
            btnUpgradeNow.tag = "upgrade"
            binding.tvWeekly.setTextColor(
                ContextCompat.getColor(
                    this@PremiumActivity,
                    R.color.text_color_p
                )
            )
            binding.weeklyPrice.setTextColor(
                ContextCompat.getColor(
                    this@PremiumActivity,
                    R.color.text_color_p
                )
            )
            binding.tvYearly.setTextColor(
                ContextCompat.getColor(
                    this@PremiumActivity,
                    R.color.text_color_s
                )
            )
            binding.yearlyPrice.setTextColor(
                ContextCompat.getColor(
                    this@PremiumActivity,
                    R.color.text_color_s
                )
            )
            binding.hbtnSwitchCompact.isChecked = false
            pos = 0
            conWeeklyPlan.background =
                ContextCompat.getDrawable(this@PremiumActivity, R.drawable.weekly_plan_bg_s)
            conYearlyPlan.background =
                ContextCompat.getDrawable(this@PremiumActivity, R.drawable.yearly_plan_bg)
        }
    }

    private fun setYearlyClickedUi() {
        binding.apply {
            binding.tvWeekly.setTextColor(
                ContextCompat.getColor(
                    this@PremiumActivity,
                    R.color.text_color_s
                )
            )
            binding.weeklyPrice.setTextColor(
                ContextCompat.getColor(
                    this@PremiumActivity,
                    R.color.text_color_s
                )
            )
            binding.tvYearly.setTextColor(
                ContextCompat.getColor(
                    this@PremiumActivity,
                    R.color.text_color_p
                )
            )
            binding.yearlyPrice.setTextColor(
                ContextCompat.getColor(
                    this@PremiumActivity,
                    R.color.text_color_p
                )
            )
            pos = 1
            conYearlyPlan.background =
                ContextCompat.getDrawable(this@PremiumActivity, R.drawable.yearly_plan_bg_s)
            conWeeklyPlan.background =
                ContextCompat.getDrawable(this@PremiumActivity, R.drawable.weekly_plan_bg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadAppOpen = true
    }
}