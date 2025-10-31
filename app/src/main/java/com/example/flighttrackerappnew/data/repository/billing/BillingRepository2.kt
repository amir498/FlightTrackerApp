package com.example.flighttrackerappnew.data.repository.billing

import android.app.Activity
import android.content.Context
import android.util.Log
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
import com.example.flighttrackerappnew.BuildConfig
import com.example.flighttrackerappnew.presentation.google_play_billing.BillingEvent
import com.example.flighttrackerappnew.presentation.google_play_billing.Security
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.SUB_WEEKLY
import com.example.flighttrackerappnew.presentation.utils.SUB_WEEKLY_SALE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

class BillingRepository2(
    private val context: Context,
    private val config: Config
) {
    private var billingClient: BillingClient? = null
    private var retryCount = 0
    private val maxRetries = 3
    private var productsDetailsList: MutableList<ProductDetails>? = null
    private val _billingEvents = MutableSharedFlow<BillingEvent>()
    val billingEvents: SharedFlow<BillingEvent> = _billingEvents

    private suspend fun emitEvent(event: BillingEvent) {
        _billingEvents.emit(event)
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.AlreadyOwned(true))
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.FeatureNotSupported("FeatureNotSupported"))
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.BillingUnavailable("Billing UnAvailable Right Now"))
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.NETWORK_ERROR) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.NetworkError("Check Your Internet Connection"))
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.UserCancelled("User Cancelled"))
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

    private fun handlePurchase(purchase: Purchase) {
        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.Error("Invalid purchase signature"))
            }
            return
        }
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.PurchaseSuccess(purchase))
            }

            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase.purchaseToken)
            }
        }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                CoroutineScope(Dispatchers.IO).launch {
                    emitEvent(BillingEvent.PurchaseAcknowledged(purchaseToken))
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    emitEvent(
                        BillingEvent.Error(
                            "Acknowledge failed: ${billingResult.debugMessage}"
                        )
                    )
                }
            }
        }
    }

    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            val security = Security()
            security.verifyPurchase(BuildConfig.BASE_64_KEY, signedData, signature)
        } catch (_: IOException) {
            false
        }
    }

    fun startConnection(func: (() -> Unit)) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    func()
                }
            }

            override fun onBillingServiceDisconnected() {
                retryConnection(func)
            }
        }) ?: run {
            initializeBillingClient()
            startConnection { func }
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

    fun launchPurchaseFlow(
        activity: Activity,
        pos: Int
    ) {
        if (productsDetailsList.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.IsProductFound(false))
            }
            return
        }

        val productDetails = productsDetailsList!![pos]

        val selectedOfferToken =
            productDetails.subscriptionOfferDetails
                ?.firstOrNull { it.offerId == null }
                ?.offerToken

        if (selectedOfferToken == null) {
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.Error("No valid offerToken found"))
            }
            return
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(selectedOfferToken)
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }

    fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUB_WEEKLY_SALE)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SUB_WEEKLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, productDetailList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productsDetailsList = productDetailList.productDetailsList
                CoroutineScope(Dispatchers.IO).launch {
                    extractPricingDetails(productsDetailsList as List<ProductDetails>)
                    emitEvent(BillingEvent.ProductDetailsLoaded(true))
                    showLog(productsDetailsList as List<ProductDetails>)
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    emitEvent(BillingEvent.Error(billingResult.debugMessage.toString()))
                }
            }
        }
    }

    private fun extractPricingDetails(productDetailsList: List<ProductDetails>) {
        var weeklyNormalPrice: String? = null
        var weeklySalePrice: String? = null

        productDetailsList.forEach { productDetails ->
            val productId = productDetails.productId

            val pricingPhases = productDetails.subscriptionOfferDetails
                ?.flatMap { it.pricingPhases.pricingPhaseList }
                ?: emptyList()

            pricingPhases.forEach { phase ->
                val priceStr = phase.formattedPrice

                if (phase.billingPeriod == "P1W") {
                    when (productId) {
                        "premium_weekly" -> {
                            weeklyNormalPrice = priceStr
                        }

                        "premium_weekly_sale" -> {
                            weeklySalePrice = "$priceStr/Week"
                        }
                    }
                }
            }
        }
        config.apply {
            priceWeekly = weeklyNormalPrice ?: "$1.99/Week"
            priceWeeklySale = weeklySalePrice ?: "$1.212/Week"

            Log.d("MY--TAG", "priceWeekly: $priceWeekly")
            Log.d("MY--TAG", "priceWeeklySale: $priceWeeklySale")
        }
    }

    private fun showLog(productDetailsList: List<ProductDetails>) {
        productDetailsList.forEachIndexed { index, productDetails ->

            Log.d("MY--TAG", "------ Product $index ------")
            Log.d("MY--TAG", "Product ID: ${productDetails.productId}")
            Log.d("MY--TAG", "Name: ${productDetails.name}")
            Log.d("MY--TAG", "Description: ${productDetails.description}")
            Log.d("MY--TAG", "Product Type: ${productDetails.productType}")

            productDetails.subscriptionOfferDetails?.forEach { offer ->
                Log.d("MY--TAG", "Offer Tags: ${offer.offerTags}")
                Log.d("MY--TAG", "Base Plan ID: ${offer.basePlanId}")
                Log.d("MY--TAG", "Offer ID: ${offer.offerId}")

                offer.pricingPhases.pricingPhaseList.forEach { phase ->
                    Log.d("MY--TAG", "Pricing Phase:")
                    Log.d("MY--TAG", "  Price: ${phase.formattedPrice}")
                    Log.d("MY--TAG", "  Billing Period: ${phase.billingPeriod}")
                    Log.d("MY--TAG", "  Recurrence Mode: ${phase.recurrenceMode}")
                    Log.d("MY--TAG", "  Billing Cycle Count: ${phase.billingCycleCount}")
                }
            }
        }
    }

    fun queryPurchase() {
        billingClient!!.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS).build()
        ) { _, purchaseList ->
            config.isPremiumUser = purchaseList.isNotEmpty()
        }
    }

    fun restorePurchase() {
        billingClient!!.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS).build()
        ) { _, purchaseList ->
            CoroutineScope(Dispatchers.IO).launch {
                emitEvent(BillingEvent.RestorePurchaseResult(purchaseList))
            }
            config.isPremiumUser = purchaseList.isNotEmpty()
        }
    }
}