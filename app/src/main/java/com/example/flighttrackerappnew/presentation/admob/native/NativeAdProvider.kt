package com.example.flighttrackerappnew.presentation.admob.native

import com.example.flighttrackerappnew.FlightApp
import com.example.flighttrackerappnew.R

object NativeAdProvider {
    private val app = FlightApp.Companion.instance
    val native_1_LANGUAGE_SCREEN1 =
        NativeAdsManager(
            app.getString(R.string.native_1_LANGUAGE_SCREEN1) to "native_1_LANGUAGE_SCREEN1",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val native_2_LANGUAGE_SCREEN1 =
        NativeAdsManager(
            app.getString(R.string.native_2_LANGUAGE_SCREEN1) to "native_2_LANGUAGE_SCREEN1",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val native_1_LANGUAGE_SCREEN2 =
        NativeAdsManager(
            app.getString(R.string.native_1_LANGUAGE_SCREEN2) to "native_1_LANGUAGE_SCREEN2",
            adType = NativeAdTypes.NATIVE_LARGE
        )
    val native_2_LANGUAGE_SCREEN2 =
        NativeAdsManager(
            app.getString(R.string.native_2_LANGUAGE_SCREEN2) to "native_2_LANGUAGE_SCREEN2",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val native_OnBoarding1 =
        NativeAdsManager(
            app.getString(R.string.native_OnBoarding1) to "native_OnBoarding1",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val native_OnBoarding2 =
        NativeAdsManager(
            app.getString(R.string.native_OnBoarding2) to "native_OnBoarding2",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val native_OnBoarding4 =
        NativeAdsManager(
            app.getString(R.string.native_OnBoarding4) to "native_OnBoarding4",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val native_OnBoarding3 =
        NativeAdsManager(
            app.getString(R.string.native_OnBoarding3) to "native_OnBoarding3",
            adType = NativeAdTypes.NATIVE_FULL
        )

    val native_OnBoarding5 =
        NativeAdsManager(
            app.getString(R.string.native_OnBoarding5) to "native_OnBoarding5",
            adType = NativeAdTypes.NATIVE_FULL
        )

    val native_OnBoarding6 =
        NativeAdsManager(
            app.getString(R.string.native_OnBoarding6) to "native_OnBoarding6",
            adType = NativeAdTypes.NATIVE_FULL
        )

    val NATIVE_WELCOME =
        NativeAdsManager(
            app.getString(R.string.NATIVE_WELCOME) to "NATIVE_WELCOME",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_WELCOME2 =
        NativeAdsManager(
            app.getString(R.string.NATIVE_WELCOME) to "NATIVE_WELCOME2",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_MAP =
        NativeAdsManager(
            app.getString(R.string.NATIVE_MAP) to "NATIVE_MAP",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_MAP2 =
        NativeAdsManager(
            app.getString(R.string.NATIVE_MAP) to "NATIVE_MAP2",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_HOME =
        NativeAdsManager(
            app.getString(R.string.NATIVE_HOME) to "NATIVE_HOME",
            adType = NativeAdTypes.NATIVE_SMALL
        )

    val NATIVE_SAVED_FLIGHT =
        NativeAdsManager(
            app.getString(R.string.NATIVE_SAVED_FLIGHT) to "NATIVE_SAVED_FLIGHT",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_FLIGHT_SCHEDULED =
        NativeAdsManager(
            app.getString(R.string.NATIVE_FLIGHT_SCHEDULED) to "NATIVE_FLIGHT_SCHEDULED",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_FLIGHT_SCHEDULED_SEARCH =
        NativeAdsManager(
            app.getString(R.string.NATIVE_FLIGHT_SCHEDULED_SEARCH) to "NATIVE_FLIGHT_SCHEDULED_SEARCH",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_FLIGHT_SCHEDULED_TYPE =
        NativeAdsManager(
            app.getString(R.string.NATIVE_FLIGHT_SCHEDULED_TYPE) to "NATIVE_FLIGHT_SCHEDULED_TYPE",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_SEARCH_ACTIVITY =
        NativeAdsManager(
            app.getString(R.string.NATIVE_SEARCH_ACTIVITY) to "NATIVE_SEARCH_ACTIVITY",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_SETTING =
        NativeAdsManager(
            app.getString(R.string.NATIVE_SETTING) to "NATIVE_SETTING",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_TRACKED_FLIGHT =
        NativeAdsManager(
            app.getString(R.string.NATIVE_TRACKED_FLIGHT) to "NATIVE_TRACKED_FLIGHT",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline =
        NativeAdsManager(
            app.getString(R.string.NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline) to "NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline",
            adType = NativeAdTypes.NATIVE_LARGE,
            nativeAdCategory = NativeAdCategory.NATIVE_RECYCLERVIEW
        )

    val NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber =
        NativeAdsManager(
            app.getString(R.string.NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber) to "NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber",
            adType = NativeAdTypes.NATIVE_LARGE
        )

    val NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline =
        NativeAdsManager(
            app.getString(R.string.NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline) to "NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline",
            adType = NativeAdTypes.NATIVE_LARGE,
            nativeAdCategory = NativeAdCategory.NATIVE_RECYCLERVIEW
        )

    val NATIVE_DEPARTURE_FLIGHT_For_Aircraft_Or_TailNumber =
        NativeAdsManager(
            app.getString(R.string.NATIVE_DEPARTURE_FLIGHT_For_Aircraft_Or_TailNumber) to "NATIVE_DEPARTURE_FLIGHT_For_Aircraft_Or_TailNumber",
            adType = NativeAdTypes.NATIVE_LARGE
        )
}