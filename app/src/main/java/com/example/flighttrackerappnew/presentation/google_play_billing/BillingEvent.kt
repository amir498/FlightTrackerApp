package com.example.flighttrackerappnew.presentation.google_play_billing

import com.android.billingclient.api.Purchase

sealed class BillingEvent {
    data class PurchaseSuccess(val purchase: Purchase) : BillingEvent()
    data class PurchaseAcknowledged(val purchaseToken: String) : BillingEvent()
    data class ProductDetailsLoaded(val loaded: Boolean) : BillingEvent()
    data class Error(val message: String) : BillingEvent()
    data class BillingUnavailable(val message: String) : BillingEvent()
    data class NetworkError(val message: String) : BillingEvent()
    data class UserCancelled(val message: String) : BillingEvent()
    data class FeatureNotSupported(val message: String) : BillingEvent()
    data class AlreadyOwned(val isAlreadyOwned: Boolean) : BillingEvent()
    data class IsProductFound(val isProductFound: Boolean) : BillingEvent()
    data class QueryPurchaseResult(val purchaseList: MutableList<Purchase>) : BillingEvent()
    data class RestorePurchaseResult(val purchaseList: MutableList<Purchase>) : BillingEvent()
}
