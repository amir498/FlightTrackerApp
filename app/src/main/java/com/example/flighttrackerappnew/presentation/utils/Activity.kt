package com.example.flighttrackerappnew.presentation.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.net.toUri

fun Activity.setScreenDisplay() {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars =
        false
}

fun Activity.setFullscreenCompat(fullScreen: Boolean) {
    if (isOreoMr1Plus()) {
        WindowCompat.getInsetsController(window, window.decorView.rootView)
            .hide(WindowInsetsCompat.Type.statusBars())
    } else {
        val flagToUpdate = WindowManager.LayoutParams.FLAG_FULLSCREEN
        if (fullScreen) {
            window.addFlags(flagToUpdate)
        } else {
            window.clearFlags(flagToUpdate)
        }
    }
}

fun Activity.shareApp() {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
        putExtra(
            Intent.EXTRA_TEXT,
            "Hey, check out this awesome app: https://play.google.com/store/apps/details?id=$packageName"
        )
    }
    startActivity(Intent.createChooser(shareIntent, "Share via"))
}

fun Activity.openPrivacyPolicy() {
    val privacyUrl = "https://sites.google.com/view/privacypolicyfindmyphone/home"
    val intent = Intent(Intent.ACTION_VIEW, privacyUrl.toUri())
    startActivity(intent)
}

fun Activity.rateApp() {
    val uri = "market://details?id=$packageName".toUri()
    val goToMarket = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
    }

    try {
        startActivity(goToMarket)
    } catch (_: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()
            )
        )
    }
}
