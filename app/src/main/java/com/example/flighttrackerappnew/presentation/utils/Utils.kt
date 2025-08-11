package com.example.flighttrackerappnew.presentation.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun formatTo12HourTime(input: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        val outputFormat = SimpleDateFormat("h:mm a", Locale.US)
        val date: Date = inputFormat.parse(input)!!
        outputFormat.format(date)
    } catch (_: Exception) {
        "N/A"
    }
}


fun getFlightProgressPercent(dep: String, arr: String): Int {
    if (dep == "N/A" || arr == "N/A") return 0

    val baseDate = "01/01/1970"
    val fullFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault())
    fullFormat.isLenient = false

    return try {
        val depTime = fullFormat.parse("$baseDate $dep") ?: return 0
        var arrTime = fullFormat.parse("$baseDate $arr") ?: return 0
        val now = Date()

        // Handle overnight arrival
        if (arrTime.before(depTime)) {
            val calendar = Calendar.getInstance()
            calendar.time = arrTime
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            arrTime = calendar.time
        }

        // If current time is equal to or after arrival time, progress is 100
        if (now.time >= arrTime.time) return 100

        val totalDuration = arrTime.time - depTime.time
        val elapsed = now.time - depTime.time

        if (totalDuration <= 0) return 0
        if (elapsed <= 0) return 0

        val progress = (elapsed.toDouble() / totalDuration) * 100
        progress.coerceIn(0.0, 100.0).toInt()

    } catch (e: Exception) {
        0
    }
}

fun getTimeDifference(dep: String, arr: String): String {
    if (dep == "N/A" || arr == "N/A") return "N/A"

    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    format.isLenient = false

    return try {
        val depTime = format.parse(dep)
        val arrTime = format.parse(arr)

        if (depTime == null || arrTime == null) return "N/A"

        var diff = arrTime.time - depTime.time

        if (diff < 0) diff += TimeUnit.DAYS.toMillis(1)

        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60

        when {
            hours > 0 && minutes > 0 -> "$hours hrs $minutes mins"
            hours > 0 -> "$hours hrs"
            minutes > 0 -> "$minutes mins"
            else -> "$seconds secs"
        }

    } catch (e: Exception) {
        "N/A"
    }
}

fun formatIsoDate(input: String): String {
    try {
        Log.d("MY--TAG", "formatIsoDate:$input")
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        isoFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date: Date = isoFormat.parse(input)!!

        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return outputFormat.format(date)
    } catch (_: RuntimeException) {
        return "N/A"
    }catch (_:ParseException){
        return "N/A"
    }
}

fun formatToPrettyDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())

    return try {
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString // fallback if parsing fails
    }
}


fun openGoogleMap(lat: String, long: String, context: Context) {
    try {
        val latitude = lat.toDouble()
        val longitude = long.toDouble()

        val label = "Saved Location"
        val uri = "http://maps.google.com/maps?q=loc:$latitude,$longitude($label)".toUri()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            context.showToast("Google Maps not found")
        }
    } catch (e: NumberFormatException) {
        context.showToast("N/A")
    }
}

