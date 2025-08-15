package com.example.flighttrackerappnew.data.repository.billing

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener

class BillingRepository(
    private val context: Context
) : PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null

    fun startConnection(onReady: () -> Unit) {
        if (billingClient?.isReady == true) {
            onReady()
            return
        }

        billingClient = BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onReady()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try reconnecting when service disconnected
            }
        })
    }

    fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {

        }
    }

}