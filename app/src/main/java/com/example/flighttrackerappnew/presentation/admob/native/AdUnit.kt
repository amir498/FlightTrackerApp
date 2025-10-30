package com.example.flighttrackerappnew.presentation.admob.native

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AdUnit<AdObject, AdType>(
    val id: String,
    val name: String,
    val adType: AdType
) {

    var ad: AdObject? = null

    val _statusFlow = MutableStateFlow(AdStatus.None)
    val statusFlow: StateFlow<AdStatus> = _statusFlow.asStateFlow()
    val status: AdStatus
        get() = statusFlow.value

    fun shouldLoadAd(): Boolean {
        return when (status) {
            AdStatus.None,
            AdStatus.Failure -> true
            AdStatus.Loading -> false
            AdStatus.Ready -> false
            AdStatus.Shown -> true
        }
    }
}