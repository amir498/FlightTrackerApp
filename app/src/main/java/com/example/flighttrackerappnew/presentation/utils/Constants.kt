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
const val SUB_WEEKLY = "premium_weekly"
const val SUB_YEARLY = "premium_yearly"
const val PRIVACY_POLICY = "https://sites.google.com/view/tanydev-flight-tracker"
const val MORE_APPS = "https://play.google.com/store/apps/dev?id=7232886238187016302"
const val TERM_OF_SERVICE = "https://sites.google.com/view/term-of-service-flight-tracker"
const val PREMIUM_USER = "PREMIUM_USER"
const val SAVE_VALUE = "SAVE_VALUE"
const val FREE_TRAIL = "FREE_TRAIL"
