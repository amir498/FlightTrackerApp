package com.example.liveflighttrackerapp.presentation.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

const val PREFS_KEY = "Prefs"