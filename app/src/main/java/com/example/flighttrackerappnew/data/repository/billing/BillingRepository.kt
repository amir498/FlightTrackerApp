package com.example.flighttrackerappnew.data.repository.billing

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
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.google_play_billing.Security
import com.example.flighttrackerappnew.presentation.utils.NotPremiumUser
import com.example.flighttrackerappnew.presentation.utils.showToast
import java.io.IOException

class BillingRepository(
    private val context: Context
) {
    private var billingClient: BillingClient? = null
    private var retryCount = 0
    private val maxRetries = 3

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            NotPremiumUser = false
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
            context.showToast("FEATURE_NOT_SUPPORTED")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            context.showToast("Billing UnAvailable")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR) {
            context.showToast("Network Error")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            context.showToast("Subscription Cancelled")
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
                    PendingPurchasesParams.newBuilder().enablePrepaidPlans().build()
                )
                .build()
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

    private fun acknowledgePurchase(purchaseToken: String) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                NotPremiumUser = false
            } else {
                NotPremiumUser = true
            }
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

    fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
    }

    private fun retryConnection(func: () -> Unit) {
        if (retryCount < maxRetries) {
            retryCount++
            startConnection(func)
        }
    }

    private var productsDetailsList: MutableList<ProductDetails>? = null

    fun launchPurchaseFlow(
        activity: Activity,
        pos: Int,
    ) {
        if (productsDetailsList?.size == 0 || productsDetailsList == null) {
            queryProductDetails()
            context.showToast("No Product Found Check Internet Connection")
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

    fun queryProductDetails() {
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

//        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
//            productsDetailsList = productDetailsList
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
//                extractPricingDetails(productDetailsList)
//            }
//        }
    }

    private fun extractPricingDetails(
        productDetailsList: List<ProductDetails>
    ) {
        val pricingMap = mutableMapOf<String, String>()
        var hasFreeTrial = true

        productDetailsList.forEachIndexed { index, productDetails ->
            val offerDetails = productDetails.subscriptionOfferDetails
            if (index == 2) {
                offerDetails?.let {
                    hasFreeTrial = offerDetails.size != 1
                }
            }
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
//        sharedPreferenceManager.savedStringValue(
//            "PriceWeekly", pricingMap["weekly"] ?: "$1.99/Week"
//        )
//        sharedPreferenceManager.savedStringValue(
//            "PriceMonthly", pricingMap["monthly"] ?: "$3.99/Month"
//        )
//        sharedPreferenceManager.savedStringValue(
//            "PriceYearly", pricingMap["yearly"] ?: "$17.99/Year"
//        )
//        sharedPreferenceManager.savedBooleanValue(
//            "showFreeTrail", hasFreeTrial
//        )
//        activityViewModel.getPremiumPrice(pricingMap)
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