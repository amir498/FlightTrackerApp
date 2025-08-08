package com.example.flighttrackerappnew.presentation.dialogbuilder

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import com.example.flighttrackerappnew.R

class CustomDialogBuilder(private val context: Context) {

    private var layoutResId: Int = 0
    private var isCancelable: Boolean = true
    private var onPositiveClick: ((dialog: Dialog) -> Unit)? = null
    private var onNegativeClick: ((dialog: Dialog) -> Unit)? = null

    fun setLayout(@LayoutRes layout: Int): CustomDialogBuilder {
        this.layoutResId = layout
        return this
    }

    fun setCancelable(cancelable: Boolean): CustomDialogBuilder {
        this.isCancelable = cancelable
        return this
    }

    fun setPositiveClickListener(listener: (dialog: Dialog) -> Unit): CustomDialogBuilder {
        this.onPositiveClick = listener
        return this
    }

    fun setNegativeClickListener(listener: (dialog: Dialog) -> Unit): CustomDialogBuilder {
        this.onNegativeClick = listener
        return this
    }

    fun show(fullScreen: Boolean = false): Dialog {
        val view = LayoutInflater.from(context).inflate(layoutResId, null)
        val dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(view)
            setCancelable(isCancelable)
            window?.setBackgroundDrawableResource(R.color.transparent)
        }


        if (fullScreen){
            setDialogFullScreen(dialog)
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

    private fun setDialogFullScreen(dialog: Dialog) {
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}