package com.example.flighttrackerappnew.presentation.enums

import com.example.flighttrackerappnew.R

enum class MapOptionSelected(val option1: Int, val option2: Int) {
    SELECTED_OPTION1(
        R.drawable.surface_bg_s,
        R.drawable.surface_bg
    ),
    SELECTED_OPTION2(
        R.drawable.surface_bg,
        R.drawable.surface_bg_s
    )
}