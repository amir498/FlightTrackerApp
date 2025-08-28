package com.example.flighttrackerappnew.presentation.utils

import android.animation.TimeInterpolator
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.visible() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        visibility = View.VISIBLE
    } else {
        post { visibility = View.VISIBLE }
    }
}

fun View.invisible() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        visibility = View.INVISIBLE
    } else {
        post { visibility = View.INVISIBLE }
    }
}

fun View.gone() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        visibility = View.GONE
    } else {
        post { visibility = View.GONE }
    }
}

fun View.setZoomClickEffect(scaleFactor: Float = 1.02f, duration: Long = 100) {
    val interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.animate()
                    .scaleX(scaleFactor)
                    .scaleY(scaleFactor)
                    .setDuration(duration)
                    .setInterpolator(interpolator)
                    .start()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                v.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration)
                    .setInterpolator(interpolator)
                    .start()
                if (event.action == MotionEvent.ACTION_UP) {
                    v.performClick()
                }
            }
        }
        true
    }
}

fun View.getStatusBarHeight(): Int {
    val insets = ViewCompat.getRootWindowInsets(this)
    return insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
}

fun View.getNavigationBarHeight(): Int {
    val insets = ViewCompat.getRootWindowInsets(this)
    return insets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0
}