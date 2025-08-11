package com.example.liveflighttrackerapp.presentation.googleMap

import com.example.liveflighttrackerapp.data.model.airport.AirportsDataItems
import com.example.liveflighttrackerapp.data.model.flight.FlightDataItem
import com.example.liveflighttrackerapp.presentation.listener.AirPlaneClickListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MyGoogleMap : OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    fun setMapUi(supportMapFragment: SupportMapFragment) {
        supportMapFragment.getMapAsync(this)
    }

    private val drawnFlightPaths = mutableMapOf<String, Polyline>()

    private var airPlaneClickListener: AirPlaneClickListener? = null


    fun clearAllFlightPaths() {
        drawnFlightPaths.values.forEach { it.remove() }
        drawnFlightPaths.clear()
    }

    private val drawnMarkers = mutableListOf<Marker>()

    private fun clearAllMarkers() {
        drawnMarkers.forEach { it.remove() }
        drawnMarkers.clear()
    }

    fun drawFlightPathIfNotExists(
        flightData: FlightDataItem,
        departure: AirportsDataItems?,
        arrival: AirportsDataItems?
    ) {
        if (departure == null || arrival == null) return

        clearAllFlightPaths()
        clearAllMarkers()

        val currentFlightLocation = LatLng(
            flightData.geography?.latitude ?: return,
            flightData.geography.longitude ?: return
        )

        val departureLatLng = LatLng(
            departure.latitudeAirport ?: return,
            departure.longitudeAirport ?: return
        )

        val arrivalLatLng = LatLng(
            arrival.latitudeAirport ?: return,
            arrival.longitudeAirport ?: return
        )

        val flightId = flightData.flight?.iataNumber ?: return

        val line1 = mMap?.addPolyline(
            PolylineOptions()
                .add(departureLatLng, currentFlightLocation)
                .width(4f)
                .color(android.graphics.Color.BLUE)
                .geodesic(true)
        )

        val line2 = mMap?.addPolyline(
            PolylineOptions()
                .add(currentFlightLocation, arrivalLatLng)
                .width(4f)
                .color(android.graphics.Color.RED)
                .geodesic(true)
        )

        val departureMarker = mMap?.addMarker(
            MarkerOptions()
                .position(departureLatLng)
                .title("Departure: ${departure.nameAirport ?: "Unknown"}")
        )

        val arrivalMarker = mMap?.addMarker(
            MarkerOptions()
                .position(arrivalLatLng)
                .title("Arrival: ${arrival.nameAirport ?: "Unknown"}")
        )

        departureMarker?.let { drawnMarkers.add(it) }
        arrivalMarker?.let { drawnMarkers.add(it) }

        if (line1 != null && line2 != null) {
            drawnFlightPaths[flightId] = line1
            drawnFlightPaths["${flightId}_2"] = line2
        }
    }

    private var onCameraIdleCallback: ((LatLngBounds?) -> Unit)? = null
    private var onCameraMoveStartedCallback: ((Int) -> Unit)? = null

    fun onCameraIdle(callback: (LatLngBounds?) -> Unit) {
        this.onCameraIdleCallback = callback
    }

    fun setOnCameraMoveStartedListener(callback: (Int) -> Unit) {
        onCameraMoveStartedCallback = callback
    }

    fun listener(airPlaneClickListener: AirPlaneClickListener) {
        this.airPlaneClickListener = airPlaneClickListener
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.setOnMarkerClickListener { marker ->
            val flightData = marker.tag as? FlightDataItem
            flightData?.let { airPlaneClickListener?.onPlaneClick(it) }
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

    fun getVisibleBounds(): LatLngBounds? {
        return mMap?.projection?.visibleRegion?.latLngBounds
    }

    fun addPlaneMarker(
        latitude: Double,
        longitude: Double,
        markerIcon: BitmapDescriptor?,
        direction: Double?,
        flightData: FlightDataItem? = null
    ) {
        val planePosition = LatLng(latitude, longitude)

        val marker = mMap?.addMarker(
            MarkerOptions()
                .position(planePosition)
                .icon(markerIcon)
                .rotation(direction?.toFloat() ?: 0f)
                .anchor(0.5f, 0.5f)
                .flat(true)
                .title("Plane")
        )

        marker?.tag = flightData
        marker?.let { drawnMarkers.add(it) }
    }

    fun removeMarkersOutsideVisibleRegion() {
        val bounds = getVisibleBounds() ?: return
        val iterator = drawnMarkers.iterator()

        while (iterator.hasNext()) {
            val marker = iterator.next()
            if (!bounds.contains(marker.position)) {
                marker.remove()
                iterator.remove()
            }
        }
    }
}