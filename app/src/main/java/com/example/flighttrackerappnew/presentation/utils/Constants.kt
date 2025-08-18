package com.example.flighttrackerappnew.presentation.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import com.google.android.gms.maps.GoogleMap

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

const val PREFS_KEY = "Prefs"
const val LangPref = "LangPref"
const val MAPStyle = "MAPStyle"
const val PRIVACY_POLICY_ACCEPTED = "PRIVACY_POLICY_ACCEPTED"
const val TOUCH_BLOCKER_TAG = "TOUCH_BLOCKER_VIEW"
