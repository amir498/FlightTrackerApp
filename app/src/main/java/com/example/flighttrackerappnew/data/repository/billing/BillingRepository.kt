package com.example.flighttrackerappnew.data.repository.billing

import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
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
                    PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
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

    private fun retryConnection(func: () -> Unit) {
        if (retryCount < maxRetries) {
            retryCount++
            startConnection(func)
        }
    }
}