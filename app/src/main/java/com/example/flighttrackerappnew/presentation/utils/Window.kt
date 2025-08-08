package com.example.flighttrackerappnew.presentation.utils

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

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

fun Window.setStatusAndNavigationBarColor(
    context: Context,
    colorStatusBar: Int,
    colorNavigationBar: Int
) {
    statusBarColor = ContextCompat.getColor(context, colorStatusBar)
    navigationBarColor =
        ContextCompat.getColor(context, colorNavigationBar)
}

fun Window.blockTouch() {
    val decorView = this.decorView as ViewGroup
    if (decorView.findViewWithTag<View>(TOUCH_BLOCKER_TAG) != null) return

    val blockerView = View(decorView.context).apply {
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(Color.TRANSPARENT)
        isClickable = true
        isFocusable = true
        tag = TOUCH_BLOCKER_TAG
    }

    decorView.addView(blockerView)
}

fun Window.unblockTouch() {
    val decorView = this.decorView as ViewGroup
    decorView.findViewWithTag<View>(TOUCH_BLOCKER_TAG)?.let {
        decorView.removeView(it)
    }
}