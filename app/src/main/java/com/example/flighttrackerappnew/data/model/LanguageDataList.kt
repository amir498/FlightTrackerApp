package com.example.flighttrackerappnew.data.model

data class LanguageDataList(
    val flag:Int,
    val name:String,
    val code:String,
    val isAd: Boolean = false,
    val type: Int
)
