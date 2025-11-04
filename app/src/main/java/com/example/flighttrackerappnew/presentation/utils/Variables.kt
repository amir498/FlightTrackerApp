package com.example.flighttrackerappnew.presentation.utils

import com.example.flighttrackerappnew.data.model.airplane.AirPlaneItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.google.android.gms.maps.model.LatLng

var FullDetailsFlightData: FullDetailFlightData? = null

var airportCode = ""
var flightType = "arrival"
var startDate = ""
var IS_FROM_SETTING_ACTIVITY = false
var selectedDate = ""
var lastSelectedPlane: FlightDataItem? = null
var lastArrivalLongLat: LatLng? = null
var loadAppOpen = true
var clickCount = 0
var rewardEarned = false
var lat: Double? = null
var lon: Double? = null

var isFirstPremiumFlow = false
var searchedDataSubTitle = ""
var searchedDataTitle = ""
const val DISCOUNT_START_TIME = "discount_start_time"

var isFlightTrackerApiSuccess = false
var isNearByApiSuccess = false
var isFlightScheduleApiSuccess = false
var isFutureScheduleApiSuccess = false
var isCitiesApiSuccess = false
var isAirCraftApiSuccess = false
var isAirLineApiSuccess = false
var isAirPortApiSuccess = false
var isFromAirportOrAirline = false
var selectedLiveFlightData: FullDetailFlightData? = null
var matchingAirplanes: List<AirPlaneItems> = emptyList()
