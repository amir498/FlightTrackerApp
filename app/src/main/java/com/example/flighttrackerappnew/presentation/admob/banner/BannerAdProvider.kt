package com.example.flighttrackerappnew.presentation.admob.banner

import com.example.flighttrackerappnew.FlightApp
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.admob.remote_config.RemoteConfigManager

object BannerAdProvider {
    private val app = FlightApp.Companion.instance

    val BANNER_SPLASH =
        BannerAdsManager(
            app.getString(R.string.BANNER_SPLASH) to "BANNER_SPLASH",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_SPLASH"),
            adType = BannerAdTypes.SIMPLE
        )

    val BANNER_HOME =
        BannerAdsManager(
            app.getString(R.string.BANNER_HOME) to "BANNER_HOME",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_HOME"),
            adType = BannerAdTypes.COLLAPSIBLE
        )
    val BANNER_DETAIL =
        BannerAdsManager(
            app.getString(R.string.BANNER_DETAIL) to "BANNER_DETAIL",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_DETAIL"),
            adType = BannerAdTypes.COLLAPSIBLE
        )
    val BANNER_LIVE_MAP =
        BannerAdsManager(
            app.getString(R.string.BANNER_LIVE_MAP) to "BANNER_LIVE_MAP",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_LIVE_MAP"),
            adType = BannerAdTypes.COLLAPSIBLE
        )

    val BANNER_NEARBy_AIRPORT =
        BannerAdsManager(
            app.getString(R.string.BANNER_NEARBy_AIRPORT) to "BANNER_NEARBy_AIRPORT",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_NEARBy_AIRPORT"),
            adType = BannerAdTypes.COLLAPSIBLE
        )
    val BANNER_SEARCH_AIRLINE =
        BannerAdsManager(
            app.getString(R.string.BANNER_SEARCH_AIRLINE) to "BANNER_SEARCH_AIRLINE",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_SEARCH_AIRLINE"),
            adType = BannerAdTypes.COLLAPSIBLE
        )

    val BANNER_SEARCH_AIRCRAFT =
        BannerAdsManager(
            app.getString(R.string.BANNER_SEARCH_AIRCRAFT) to "BANNER_SEARCH_AIRCRAFT",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_SEARCH_AIRCRAFT"),
            adType = BannerAdTypes.COLLAPSIBLE
        )

    val BANNER_SEARCH_AIRPORT =
        BannerAdsManager(
            app.getString(R.string.BANNER_SEARCH_AIRPORT) to "BANNER_SEARCH_AIRPORT",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_SEARCH_AIRPORT"),
            adType = BannerAdTypes.COLLAPSIBLE
        )

    val BANNER_SEARCH_TAIL =
        BannerAdsManager(
            app.getString(R.string.BANNER_SEARCH_TAIL) to "BANNER_SEARCH_TAIL",
            isBannerEnabled = RemoteConfigManager.getBoolean("BANNER_SEARCH_TAIL"),
            adType = BannerAdTypes.COLLAPSIBLE
        )
}