package com.example.flighttrackerappnew.presentation.utils

import android.animation.TimeInterpolator
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

fun View.visible(){
    visibility = View.VISIBLE
}

fun View.invisible(){
    visibility = View.INVISIBLE
}

fun View.gone(){
    visibility = View.GONE
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