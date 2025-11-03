package com.example.flighttrackerappnew.presentation.helper

import android.content.Context
import androidx.core.content.edit
import com.example.flighttrackerappnew.presentation.utils.DISCOUNT_START_TIME
import com.example.flighttrackerappnew.presentation.utils.FREE_TRAIL
import com.example.flighttrackerappnew.presentation.utils.MAPStyle
import com.example.flighttrackerappnew.presentation.utils.SAVE_VALUE
import com.example.flighttrackerappnew.presentation.utils.SUB_WEEKLY
import com.example.flighttrackerappnew.presentation.utils.SUB_WEEKLY_SALE
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

    var priceWeekly: String
        get() = prefs?.getString(SUB_WEEKLY, "$1.99/Week") ?: "$1.99/Week"
        set(value) = prefs!!.edit {
            putString(SUB_WEEKLY, value)
        }

    var priceWeeklySale: String
        get() = prefs?.getString(SUB_WEEKLY_SALE, "$1.212/Week") ?: "$1.212/Week"
        set(value) = prefs!!.edit {
            putString(SUB_WEEKLY_SALE, value)
        }

    var priceYearly: String
        get() = prefs?.getString(SUB_YEARLY, "$15.99/Year") ?: "$15.99/Week"
        set(value) = prefs!!.edit {
            putString(SUB_YEARLY, value)
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

    var discountStartTime: Long
        get() = prefs!!.getLong(DISCOUNT_START_TIME, 0L)
        set(value) = prefs!!.edit { putLong(DISCOUNT_START_TIME, value) }

    private val discountDuration = 2 * 24 * 60 * 60 * 1000L


    fun isDiscountActive(): Boolean {
        val start = discountStartTime
        if (start == 0L) return false
        val elapsed = System.currentTimeMillis() - start
        return elapsed < discountDuration
    }

    fun startDiscountIfNeeded() {
        if (discountStartTime == 0L) {
            discountStartTime = System.currentTimeMillis()
        }
    }

    fun getRemainingDiscountTime(): Long {
        val start = discountStartTime
        if (start == 0L) return 0L
        val elapsed = System.currentTimeMillis() - start
        val remaining = discountDuration - elapsed
        return if (remaining > 0) remaining else 0L
    }
}