package com.example.flighttrackerappnew.presentation.helper

import android.content.Context
import androidx.core.content.edit
import com.example.flighttrackerappnew.presentation.utils.LangPref
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
}