package com.example.flighttrackerappnew.presentation.google_play_billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.videoeditortool.R
import com.example.videoeditortool.data.sharedpreference.SharedPreferenceManager
import com.example.videoeditortool.presentation.util.NotPremiumUser
import com.example.videoeditortool.presentation.util.PREF_KEY_PREMIUM_USER
import com.example.videoeditortool.presentation.util.showLog
import com.example.videoeditortool.presentation.util.showToast
import com.example.videoeditortool.presentation.viewmodels.ActivityViewModel
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    private val context: Context,
    private val sharedPreferenceManager: SharedPreferenceManager
) {
    private var billingClient: BillingClient? = null
    private var retryCount = 0
    private val maxRetries = 3
    private var productsDetailsList: MutableList<ProductDetails>? = null
    private var fragContext: Context? = null
    private var fn: (() -> Unit?)? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            sharedPreferenceManager.savedBooleanValue(PREF_KEY_PREMIUM_USER, true)
            NotPremiumUser = false
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
            fragContext!!.showToast("FEATURE_NOT_SUPPORTED")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            fragContext!!.showToast("Billing UnAvailable")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR) {
            fragContext!!.showToast("Network Error")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            fragContext!!.showToast("Subscription Cancelled")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            return
        }
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            acknowledgePurchase(purchase.purchaseToken)
        }
    }

    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            val security = Security()
            val base64Key = context.resources.getString(R.string.base_64_key)
            security.verifyPurchase(base64Key, signedData, signature)
        } catch (e: IOException) {
            false
        }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                NotPremiumUser = false
                sharedPreferenceManager.savedBooleanValue(PREF_KEY_PREMIUM_USER, true)
                fn?.let { it() }
            } else {
                NotPremiumUser = true
                sharedPreferenceManager.savedBooleanValue(PREF_KEY_PREMIUM_USER, false)
            }
        }
    }

    init {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
                )
                .build()
        }
    }

    fun startConnection(func: (() -> Unit)) {
        if (billingClient == null) {
            initializeBillingClient()
        } else {
            billingClient?.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        func()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    retryConnection(func)
                }
            })
        }
    }

    fun queryProductDetails(activityViewModel: ActivityViewModel) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("video_editor_weekly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("video_editor_monthly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("video_editor_yearly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            productsDetailsList = productDetailsList
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                extractPricingDetails(productDetailsList, activityViewModel)
            }
        }
    }

    private fun extractPricingDetails(
        productDetailsList: List<ProductDetails>,
        activityViewModel: ActivityViewModel
    ) {
        val pricingMap = mutableMapOf<String, String>()
        var hasFreeTrial = true

        productDetailsList.forEachIndexed { index, productDetails ->
            showLog("TAGGF", "productDetails::${productDetails}")
            val offerDetails = productDetails.subscriptionOfferDetails
            showLog("TAGGF", "index::${index}")
            if (index == 2) {
                offerDetails?.let {
                    hasFreeTrial = offerDetails.size != 1
                    showLog("TAGGF", "hasFreeTrial::${hasFreeTrial}")
                }
            }
            showLog("TAGGF", "productDetailsSize::${offerDetails?.size}")
            offerDetails?.forEach { offer ->
                offer.pricingPhases.pricingPhaseList.forEach { pricingPhase ->
                    val price = pricingPhase.formattedPrice
                    when (pricingPhase.billingPeriod) {
                        "P1W" -> pricingMap["weekly"] = "$price/Week"
                        "P1M" -> pricingMap["monthly"] = "$price/Month"
                        "P1Y" -> pricingMap["yearly"] = "$price/Year"
                    }
                }
            }
        }
        sharedPreferenceManager.savedStringValue(
            "PriceWeekly", pricingMap["weekly"] ?: "$1.99/Week"
        )
        sharedPreferenceManager.savedStringValue(
            "PriceMonthly", pricingMap["monthly"] ?: "$3.99/Month"
        )
        sharedPreferenceManager.savedStringValue(
            "PriceYearly", pricingMap["yearly"] ?: "$17.99/Year"
        )
        sharedPreferenceManager.savedBooleanValue(
            "showFreeTrail", hasFreeTrial
        )
        activityViewModel.getPremiumPrice(pricingMap)
    }

    private fun retryConnection(func: () -> Unit) {
        if (retryCount < maxRetries) {
            retryCount++
            startConnection(func)
        }
    }

    fun launchPurchaseFlow(
        activityViewModel: ActivityViewModel,
        activity: Activity,
        pos: Int,
        requireContext: Context,
        fn: () -> Unit
    ) {
        this.fn = fn
        fragContext = requireContext
        if (productsDetailsList?.size == 0 || productsDetailsList == null) {
            queryProductDetails(activityViewModel)
            fragContext!!.showToast("No Product Found Check Internet Connection")
        } else {
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productsDetailsList?.get(pos)!!)
                .setOfferToken(productsDetailsList?.get(pos)?.subscriptionOfferDetails?.get(0)?.offerToken!!)
                .build()

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()

            billingClient?.launchBillingFlow(activity, billingFlowParams)
        }
    }

    fun queryPurchase(function: (MutableList<Purchase>) -> Unit) {
        billingClient!!.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS).build()
        ) { _, purchaseList ->
            function(purchaseList)
        }
    }
}
