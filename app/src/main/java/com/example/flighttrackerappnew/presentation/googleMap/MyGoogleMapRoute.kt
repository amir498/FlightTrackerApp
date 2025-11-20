package com.example.flighttrackerappnew.presentation.googleMap

import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.listener.AirPlaneClickListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLngBounds
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MyGoogleMapRoute : OnMapReadyCallback, KoinComponent {
    private var mMap: GoogleMap? = null

    private var airPlaneClickListener: AirPlaneClickListener? = null

    private var onCameraIdleCallback: ((LatLngBounds?) -> Unit)? = null
    private var onCameraMoveStartedCallback: ((Int) -> Unit)? = null

    fun listener(airPlaneClickListener: AirPlaneClickListener) {
        this.airPlaneClickListener = airPlaneClickListener
    }

    private var selectedFlight: FlightDataItem? = null

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.setOnCameraMoveStartedListener { reason ->
            onCameraMoveStartedCallback?.invoke(reason)
        }

        mMap?.setOnCameraIdleListener {
            onCameraIdleCallback?.invoke(getVisibleBounds())
        }
        mMap?.mapType = config.mapStyle
        mMap?.uiSettings?.isCompassEnabled = false
        mMap?.setOnMarkerClickListener { marker ->
            val flightData = marker.tag as? FlightDataItem
            flightData?.let {
                airPlaneClickListener?.onPlaneClick(it)
                selectedFlight = it
            }
            true
        }
    }
    private val config: Config by inject()

    fun getVisibleBounds(): LatLngBounds? {
        return mMap?.projection?.visibleRegion?.latLngBounds
    }
}