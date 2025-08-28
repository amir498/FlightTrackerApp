package com.example.flighttrackerappnew.presentation.helper

import android.content.Context
import androidx.core.content.edit
import com.example.flighttrackerappnew.presentation.utils.FREE_TRAIL
import com.example.flighttrackerappnew.presentation.utils.MAPStyle
import com.example.flighttrackerappnew.presentation.utils.PREMIUM_USER
import com.example.flighttrackerappnew.presentation.utils.PRIVACY_POLICY_ACCEPTED
import com.example.flighttrackerappnew.presentation.utils.SAVE_VALUE
import com.example.flighttrackerappnew.presentation.utils.SUB_WEEKLY
import com.example.flighttrackerappnew.presentation.utils.SUB_YEARLY
import com.google.android.gms.maps.GoogleMap

class Config(context: Context) : BaseConfig(context) {

    var mapStyle: Int
        get() = prefs?.getInt(MAPStyle, GoogleMap.MAP_TYPE_NORMAL) ?: GoogleMap.MAP_TYPE_NORMAL
        set(value) {
            prefs?.edit {
                putInt(MAPStyle, value)
            }
        }

    var isPrivacyPolicyAccepted: Boolean
        get() = prefs!!.getBoolean(PRIVACY_POLICY_ACCEPTED, false)
        set(value) {
            prefs?.edit {
                putBoolean(PRIVACY_POLICY_ACCEPTED, value)
            }
        }

    var priceWeekly: String
        get() = prefs?.getString(SUB_WEEKLY, "$1.99/Week") ?: "$1.99/Week"
        set(value) = prefs!!.edit {
            putString(SUB_WEEKLY, value)
        }

    var priceYearly: String
        get() = prefs?.getString(SUB_YEARLY, "$15.99/Year") ?: "$15.99/Week"
        set(value) = prefs!!.edit {
            putString(SUB_YEARLY, value)
        }

    var isPremiumUser: Boolean
        get() = prefs!!.getBoolean(PREMIUM_USER, false)
        set(value) = prefs!!.edit {
            putBoolean(PREMIUM_USER, value)
        }

    var isFreeTrailAvailable: Boolean
        get() = prefs!!.getBoolean(FREE_TRAIL, false)
        set(value) = prefs!!.edit {
            putBoolean(FREE_TRAIL, value)
        }

    var savePercent: String
        get() = prefs!!.getString(SAVE_VALUE, "") ?: ""
        set(value) = prefs!!.edit {
            putString(SAVE_VALUE, value)
        }
}