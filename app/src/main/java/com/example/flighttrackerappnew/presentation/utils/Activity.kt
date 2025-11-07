package com.example.flighttrackerappnew.presentation.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.flighttrackerappnew.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

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

fun initializeMobileAdsOnce(context: Context) {
    if (BuildConfig.DEBUG) {
        val testDeviceIds = listOf("AE46A43A1CB75C82FD93B9FA308DE7C3")
        val config = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(config)
    }

    MobileAds.initialize(context) {}
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

fun Activity.openWebBrowser(link: String) {
    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        showToast("No application found to open this link")
    }
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

fun Activity.setStatusBarDisplay(isStatusBarBgLight: Boolean, window: Window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            isStatusBarBgLight
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars =
            isStatusBarBgLight
    }
}
