package com.example.flighttrackerappnew.presentation.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.TextAppearanceSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat

@SuppressLint("ClickableViewAccessibility")
fun TextView.setStyledSpan(
    fullText: String,
    targetTexts: List<String>,
    @StyleRes targetStyle: Int,
    @StyleRes defaultStyle: Int,
    underline: Boolean = false,
    onClickListeners: List<(() -> Unit)> = emptyList()
) {
    val spannable = SpannableString(fullText)

    spannable.setSpan(
        TextAppearanceSpan(context, defaultStyle),
        0,
        fullText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    targetTexts.forEachIndexed { index, targetText ->
        var start = fullText.indexOf(targetText)
        while (start != -1) {
            val end = start + targetText.length

            spannable.setSpan(
                TextAppearanceSpan(context, targetStyle),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val typedArray = context.obtainStyledAttributes(targetStyle, intArrayOf(android.R.attr.fontFamily))
            val fontResId = typedArray.getResourceId(0, -1)
            typedArray.recycle()

            if (fontResId != -1) {
                val typeface = ResourcesCompat.getFont(context, fontResId)
                typeface?.let {
                    spannable.setSpan(
                        CustomTypefaceSpan(it),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            if (underline) {
                spannable.setSpan(
                    UnderlineSpan(),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (index < onClickListeners.size) {
                val clickAction = onClickListeners[index]
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        clickAction()
                    }
                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = underline
                        ds.color = currentTextColor
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            start = fullText.indexOf(targetText, end)
        }
    }

    text = spannable
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}

fun TextView.style(@StyleRes styleRes: Int) {
    setTextAppearance(styleRes)
}

fun TextView.setGradientText(startColor: Int, centerColor: Int, endColor: Int) {
    val textShader = LinearGradient(
        0f, 0f, paint.measureText(text.toString()), textSize,
        intArrayOf(startColor, centerColor, endColor),
        floatArrayOf(0f, 0.5f, 1f),
        Shader.TileMode.CLAMP
    )
    paint.shader = textShader
    invalidate()
}




