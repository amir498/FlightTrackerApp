package com.example.flighttrackerappnew.data.remoteStatus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class RemoteDataUnit(
    val name: String
) {

    var _statusFlow = MutableStateFlow(DataStatus.None)
    val statusFlow: StateFlow<DataStatus> = _statusFlow.asStateFlow()
    val status: DataStatus
        get() = statusFlow.value

    fun shouldShowDialog(): Boolean {
        return when (status) {
            DataStatus.None,
            DataStatus.Failure -> true
            DataStatus.Loading -> false
            DataStatus.SUCCESS -> false
        }
    }
}