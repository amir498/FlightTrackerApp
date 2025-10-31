package com.example.flighttrackerappnew.presentation.utils

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomTypefaceSpan(private val newTypeFace: Typeface) : MetricAffectingSpan() {
    override fun updateMeasureState(p: TextPaint) = apply(p)
    override fun updateDrawState(tp: TextPaint) = apply(tp)

    private fun apply(paint: TextPaint) {
        paint.typeface = newTypeFace
    }
}
