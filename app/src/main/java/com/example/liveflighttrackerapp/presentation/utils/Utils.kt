package com.example.liveflighttrackerapp.presentation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTo12HourTime(input: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        val outputFormat = SimpleDateFormat("h:mm a", Locale.US)
        val date: Date = inputFormat.parse(input)!!
        outputFormat.format(date)
    } catch (_: Exception) {
        "Invalid Time"
    }
}

