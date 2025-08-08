package com.example.flighttrackerappnew.presentation.enums

import com.example.flighttrackerappnew.R

enum class WelcomeOptionSelected(val option1: Int,val option2: Int,val option3: Int) {
    SELECTED_OPTION1(
        R.drawable.welcome_option_s,
        R.drawable.welcome_option_uns,
        R.drawable.welcome_option_uns
    ),
    SELECTED_OPTION2(
        R.drawable.welcome_option_uns,
        R.drawable.welcome_option_s,
        R.drawable.welcome_option_uns
    ),
    SELECTED_OPTION3(
        R.drawable.welcome_option_uns,
        R.drawable.welcome_option_uns,
        R.drawable.welcome_option_s
    )
}