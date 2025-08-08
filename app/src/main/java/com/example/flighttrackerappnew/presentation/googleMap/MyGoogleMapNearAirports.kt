package com.example.flighttrackerappnew.presentation.googleMap

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.listener.AirPortClickListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class MyGoogleMapNearAirports : OnMapReadyCallback, KoinComponent {
    private var mMap: GoogleMap? = null
    private val config: Config by inject()

    fun setMapUi(supportMapFragment: SupportMapFragment) {
        supportMapFragment.getMapAsync(this)
    }
    private var airPortClickListener: AirPortClickListener? = null

    fun listener(airPortClickListener: AirPortClickListener) {
        this.airPortClickListener = airPortClickListener
    }

    private var onCameraIdleCallback: ((LatLngBounds?) -> Unit)? = null
    private var onCameraMoveStartedCallback: ((Int) -> Unit)? = null

    fun onCameraIdle(callback: (LatLngBounds?) -> Unit) {
        this.onCameraIdleCallback = callback
    }

    fun setOnCameraMoveStartedListener(callback: (Int) -> Unit) {
        onCameraMoveStartedCallback = callback
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.mapType = config.mapStyle
        mMap?.uiSettings?.isCompassEnabled = false
        mMap?.setOnMarkerClickListener { marker ->
            val airportData = marker.tag as? NearByAirportsDataItems
            airportData?.let { airPortClickListener?.onAirportClick(it) }
            true
        }

        val pakistanCenter = LatLng(30.3753, 69.3451)
        val zoomLevel = 4.5f
        mMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(pakistanCenter, zoomLevel),
            1500,
            null
        )

        mMap?.setOnCameraMoveStartedListener { reason ->
            onCameraMoveStartedCallback?.invoke(reason)
        }

        mMap?.setOnCameraIdleListener {
            onCameraIdleCallback?.invoke(getVisibleBounds())
        }
    }

    @SuppressLint("MissingPermission")
    fun moveCameraToCurrentLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)

                val cameraPosition = CameraPosition.Builder()
                    .target(currentLatLng)
                    .zoom(4.5f)
                    .bearing(0f)
                    .tilt(45f)
                    .build()

                mMap?.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    1400,
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {}

                        override fun onCancel() {}
                    })
            } ?: run {}
        }.addOnFailureListener {}
    }

    fun getVisibleBounds(): LatLngBounds? {
        return mMap?.projection?.visibleRegion?.latLngBounds
    }
    val airportMarkers = mutableMapOf<String, Marker>()
    fun addAirportMarker(
        latitude: Double,
        longitude: Double,
        markerIcon: BitmapDescriptor?,
        direction: Double?,
        airportData: NearByAirportsDataItems
    ) {
        val position = LatLng(latitude, longitude)
        val marker = mMap?.addMarker(
            MarkerOptions()
                .position(position)
                .icon(markerIcon)
                .rotation(direction?.toFloat() ?: 0f)
                .anchor(0.5f, 0.5f)
                .flat(true)
                .title("Airport: ${airportData.nameAirport}")
        )

        airportData.codeIataAirport?.let { code ->
            airportMarkers[code] = marker!!
        }

        marker?.tag = airportData
    }
}