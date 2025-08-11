package com.example.liveflighttrackerapp.presentation.utils

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController

fun Window.hideSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}

fun Window.showSystemUI() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.show(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
    } else {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}