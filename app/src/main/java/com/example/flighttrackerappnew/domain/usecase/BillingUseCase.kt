package com.example.flighttrackerappnew.domain.usecase

import com.example.flighttrackerappnew.data.repository.billing.BillingRepository

class BillingUseCase(private val repository: BillingRepository) {

    fun initBilling(onReady: () -> Unit) {
        repository.startConnection(onReady)
    }

    fun releaseBilling() {
        repository.endConnection()
    }

}