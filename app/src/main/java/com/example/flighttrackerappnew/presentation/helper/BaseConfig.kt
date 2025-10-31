package com.example.flighttrackerappnew.presentation.helper

import android.content.Context
import androidx.core.content.edit
import com.example.flighttrackerappnew.presentation.utils.LangPref
import com.example.flighttrackerappnew.presentation.utils.PREMIUM_USER
import com.example.flighttrackerappnew.presentation.utils.PRIVACY_POLICY_ACCEPTED
import com.example.flighttrackerappnew.presentation.utils.getSharedPrefs

open class BaseConfig(context: Context) {
    protected val prefs = context.getSharedPrefs()

    var selectedLanguageCode: String
        get() = prefs?.getString(LangPref, "en") ?: "en"
        set(value) {
            prefs?.edit {
                putString(LangPref, value)
            }
        }

    var isPrivacyPolicyAccepted: Boolean
        get() = prefs!!.getBoolean(PRIVACY_POLICY_ACCEPTED, false)
        set(value) {
            prefs?.edit {
                putBoolean(PRIVACY_POLICY_ACCEPTED, value)
            }
        }

    var isPremiumUser: Boolean
        get() = prefs!!.getBoolean(PREMIUM_USER, false)
        set(value) = prefs!!.edit {
            putBoolean(PREMIUM_USER, value)
        }

}