package com.example.flighttrackerappnew.domain.usecase

import android.app.Activity
import com.example.flighttrackerappnew.data.repository.billing.BillingRepository
import com.example.flighttrackerappnew.presentation.google_play_billing.BillingEvent
import kotlinx.coroutines.flow.SharedFlow

class BillingUseCase(private val repository: BillingRepository) {
    val billingEvents: SharedFlow<BillingEvent> = repository.billingEvents

    fun startConnection(onReady: () -> Unit) {
        repository.startConnection(onReady)
    }

    fun releaseBilling() {
        repository.endConnection()
    }

    fun getProductDetails() {
        repository.queryProductDetails()
    }

    fun launchPurchases(activity: Activity, pos: Int, withTrial: Boolean) {
        repository.launchPurchaseFlow(activity, pos, withTrial)
    }

    fun queryPurchase() {
        repository.queryPurchase()
    }

    fun restorePurchase() {
        repository.restorePurchase()
    }

}