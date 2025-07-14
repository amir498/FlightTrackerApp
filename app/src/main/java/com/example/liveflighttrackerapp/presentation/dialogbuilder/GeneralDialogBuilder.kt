package com.example.liveflighttrackerapp.presentation.dialogbuilder

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.annotation.LayoutRes
import com.example.liveflighttrackerapp.R

class GeneralDialogBuilder(private val context: Context) {

    private var layoutResId: Int = 0
    private var isCancelable: Boolean = true
    private var onPositiveClick: ((dialog: Dialog) -> Unit)? = null
    private var onNegativeClick: ((dialog: Dialog) -> Unit)? = null

    fun setLayout(@LayoutRes layout: Int): GeneralDialogBuilder {
        this.layoutResId = layout
        return this
    }

    fun setCancelable(cancelable: Boolean): GeneralDialogBuilder {
        this.isCancelable = cancelable
        return this
    }

    fun setPositiveClickListener(listener: (dialog: Dialog) -> Unit): GeneralDialogBuilder {
        this.onPositiveClick = listener
        return this
    }

    fun setNegativeClickListener(listener: (dialog: Dialog) -> Unit): GeneralDialogBuilder {
        this.onNegativeClick = listener
        return this
    }

    fun show(): Dialog {
        val view = LayoutInflater.from(context).inflate(layoutResId, null)
        val dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(view)
            setCancelable(isCancelable)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        view.findViewById<View?>(R.id.positiveBtn)?.setOnClickListener {
            onPositiveClick?.invoke(dialog)
        }

        view.findViewById<View?>(R.id.negativeBtn)?.setOnClickListener {
            onNegativeClick?.invoke(dialog)
        }

        dialog.show()
        return dialog
    }
}