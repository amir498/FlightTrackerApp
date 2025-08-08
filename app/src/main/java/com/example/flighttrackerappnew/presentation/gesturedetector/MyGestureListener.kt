package com.example.flighttrackerappnew.presentation.gesturedetector

import android.view.GestureDetector
import android.view.MotionEvent
import com.example.flighttrackerappnew.presentation.listener.FlingListener
import kotlin.math.abs

class MyGestureListener(
    private val flingListener: FlingListener?
) :
    GestureDetector.SimpleOnGestureListener() {

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(
        event1: MotionEvent?,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (abs(velocityY) > abs(velocityX)) {
            if (velocityY > 0) {
                flingListener?.onFlingDown()
            } else {
                flingListener?.onFlingUp()
            }
        } else if (abs(velocityX) > abs(velocityY)) {
            if (velocityX > 0) {
                flingListener?.onFlingRight()
            } else {
                flingListener?.onFlingLeft()
            }
        }

        return true
    }
}