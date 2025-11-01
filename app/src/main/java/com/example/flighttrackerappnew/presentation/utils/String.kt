package com.example.flighttrackerappnew.presentation.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun String?.orNA(): String {
    return if (this.isNullOrBlank()) "N/A" else this
}

fun Any?.toNAString(): String = this?.toString().orNA()

fun String.extractTime(): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val date = inputFormat.parse(this)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (_: Exception) {
        ""
    }
}