package com.example.flighttrackerappnew.presentation.utils

import androidx.lifecycle.MutableLiveData
import com.example.flighttrackerappnew.data.model.FollowFlightData
import com.example.flighttrackerappnew.data.model.arrival.ArrivalDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.google.android.gms.maps.model.LatLng

var arrivalFlightData: MutableLiveData<ArrayList<ArrivalDataItems>> =
    MutableLiveData<ArrayList<ArrivalDataItems>>()
var departureFlightData: MutableLiveData<ArrayList<ArrivalDataItems>> =
    MutableLiveData<ArrayList<ArrivalDataItems>>()
var FullDetailsFlightData: FullDetailFlightData? = null
var selectedLang = ""

var airportCode = ""
var flightType = "arrival"
var startDate = ""
var allApiCallCompleted = MutableLiveData<Boolean>()
var IS_FROM_SETTING_ACTIVITY = false
var searchedDataSubTitle = ""
var searchedDataTitle = ""
var selectedDate = ""
var lastSelectedPlane:FlightDataItem? = null
var lastArrivalLongLat:LatLng? = null
var isFromDetail = false
var loadAppOpen = true
var isFromAirportOrAirline = false
var clickCount = 0
var rewardEarned = false
var isComeFromFav: Boolean = true
var favData: FullDetailFlightData? = null

var isComeFromTracked: Boolean = true
var trackData: FollowFlightData? = null