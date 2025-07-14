package com.example.liveflighttrackerapp.presentation.fragments

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.example.liveflighttrackerapp.presentation.activities.MainActivity

abstract class MyFragment<BINDING : ViewBinding>(context: Context, attr: AttributeSet) :
    ConstraintLayout(context, attr) {
    protected var activity: MainActivity? = null
    protected lateinit var binding: BINDING

    abstract fun setupFragment(activity: MainActivity)
}