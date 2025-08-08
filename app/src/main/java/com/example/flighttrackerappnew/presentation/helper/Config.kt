package com.example.flighttrackerappnew.presentation.helper

import android.content.Context
import androidx.core.content.edit
import com.example.flighttrackerappnew.presentation.utils.MAPStyle
import com.google.android.gms.maps.GoogleMap

class Config(context: Context) : BaseConfig(context) {

    var mapStyle: Int
        get() = prefs?.getInt(MAPStyle, GoogleMap.MAP_TYPE_NORMAL) ?: GoogleMap.MAP_TYPE_NORMAL
        set(value) {
            prefs?.edit {
                putInt(MAPStyle, value)
            }
        }

}