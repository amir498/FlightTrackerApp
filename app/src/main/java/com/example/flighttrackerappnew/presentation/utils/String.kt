package com.example.flighttrackerappnew.presentation.utils

fun String?.orNA(): String {
    return if (this.isNullOrBlank()) "N/A" else this
}

fun Any?.toNAString(): String = this?.toString().orNA()