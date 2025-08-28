package com.example.flighttrackerappnew.presentation.googleMap

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.listener.AirPlaneClickListener
import com.example.flighttrackerappnew.presentation.utils.lastArrivalLongLat
import com.example.flighttrackerappnew.presentation.utils.lastSelectedPlane
import com.example.flighttrackerappnew.presentation.utils.lat
import com.example.flighttrackerappnew.presentation.utils.lon
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.CancellationTokenSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class MyGoogleMap : OnMapReadyCallback, KoinComponent {
    private var mMap: GoogleMap? = null
    private val config: Config by inject()

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

    private fun clearDestinationMarkers() {
        drawnMarkers.forEach { it.remove() }
        drawnMarkers.clear()
    }

    val dashPattern: List<PatternItem> = listOf(Dash(20f), Gap(10f))

    fun drawFlightPathIfNotExists(
        flightData: FlightDataItem,
        departure: AirportsDataItems?,
        arrival: AirportsDataItems?,
        context: Context,
        arrMarkerIcon: BitmapDescriptor?,
        depMarkerIcon: BitmapDescriptor?,
        airplaneSelectedIcon: BitmapDescriptor?,
        airplaneDefaultIcon: BitmapDescriptor?,
    ) {
        if (departure == null || arrival == null) return

        clearAllFlightPaths()
        clearDestinationMarkers()

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

        val flightId = flightData.flight?.iataNumber

        val line1 = mMap?.addPolyline(
            PolylineOptions()
                .add(departureLatLng, currentFlightLocation)
                .width(4f)
                .color(ContextCompat.getColor(context, R.color.acc1))
                .geodesic(true)
                .pattern(dashPattern)
        )

        val line2 = mMap?.addPolyline(
            PolylineOptions()
                .add(currentFlightLocation, arrivalLatLng)
                .width(4f)
                .color(ContextCompat.getColor(context, R.color.route_d))
                .geodesic(true)
                .pattern(dashPattern)
        )

        withMapOnMain { map ->
            val departureMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(departureLatLng)
                    .icon(depMarkerIcon)
                    .anchor(0.5f, 0.5f)
                    .title("Departure: ${departure.nameAirport ?: "Unknown"}")
            )

            val arrivalMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(arrivalLatLng)
                    .icon(arrMarkerIcon)
                    .anchor(0.5f, 0.5f)
                    .title("Arrival: ${arrival.nameAirport ?: "Unknown"}")
            )

            departureMarker?.let { drawnMarkers.add(it) }
            arrivalMarker?.let { drawnMarkers.add(it) }

            if (line1 != null && line2 != null) {
                drawnFlightPaths[flightId.toString()] = line1
                drawnFlightPaths["${flightId}_2"] = line2
            }

            removeLastSelected()
            addLastSelectedPlane(airplaneDefaultIcon)
            removeDefaultPlane(flightData)
            addSelectedPlane(flightData, airplaneSelectedIcon, arrivalLatLng)
        }

    }

    private fun removeLastSelected() {
        val flightIdS = lastSelectedPlane?.flight?.iataNumber

        planeMarker[flightIdS]?.remove()
        planeMarker.remove(flightIdS)
    }

    private fun addLastSelectedPlane(
        airplaneSelectedIcon: BitmapDescriptor?
    ) {
        val currentFlightLocation = LatLng(
            lastSelectedPlane?.geography?.latitude ?: return,
            lastSelectedPlane?.geography?.longitude ?: return
        )
        withMapOnMain { map ->
            val marker = mMap?.addMarker(
                MarkerOptions()
                    .position(currentFlightLocation)
                    .icon(airplaneSelectedIcon)
                    .rotation((lastSelectedPlane?.geography?.direction ?: 0.0).toFloat())
                    .anchor(0.5f, 0.5f)
                    .flat(true)
                    .title("Plane")
            )

            marker?.tag = lastSelectedPlane

            if (marker != null) {
                planeMarker[lastSelectedPlane?.flight?.iataNumber.toString()] = marker
            }
        }

    }

    private fun addSelectedPlane(
        flightData: FlightDataItem?,
        airplaneSelectedIcon: BitmapDescriptor?,
        arrivalLatLng: LatLng?
    ) {
        if (flightData == null || arrivalLatLng == null) return

        val currentLat = flightData.geography?.latitude ?: return
        val currentLng = flightData.geography.longitude ?: return
        val currentFlightLocation = LatLng(currentLat, currentLng)

        val bearing = currentFlightLocation.bearingTo(arrivalLatLng)


        withMapOnMain { map ->
            val marker = mMap?.addMarker(
                MarkerOptions()
                    .position(currentFlightLocation)
                    .icon(airplaneSelectedIcon)
                    .rotation(bearing)
                    .anchor(0.5f, 0.5f)
                    .flat(true)
                    .title("Plane")
            )
            marker?.tag = flightData
            lastSelectedPlane = flightData
            lastArrivalLongLat = arrivalLatLng

            if (marker != null) {
                planeMarker[flightData.flight?.iataNumber.toString()] = marker
            }
        }
    }

    private fun withMapOnMain(block: (GoogleMap) -> Unit) {
        val map = mMap ?: return
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block(map)
        } else {
            Handler(Looper.getMainLooper()).post { block(map) }
        }
    }

    fun LatLng.bearingTo(to: LatLng): Float {
        val lat1 = Math.toRadians(this.latitude)
        val lon1 = Math.toRadians(this.longitude)
        val lat2 = Math.toRadians(to.latitude)
        val lon2 = Math.toRadians(to.longitude)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        val bearing = Math.toDegrees(atan2(y, x))
        return ((bearing + 360) % 360).toFloat()
    }

    private fun removeDefaultPlane(flightData: FlightDataItem?) {
        if (flightData == null) return
        val flightIdS = flightData.flight?.iataNumber

        planeMarker[flightIdS]?.remove()
        planeMarker.remove(flightIdS)
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

    fun zoomAtCurrentLocation() {
        if (lat == null || lon == null) return
        val pakistanCenter = LatLng(lat!!, lon!!)
        val zoomLevel = 5f
        mMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(pakistanCenter, zoomLevel),
            1500,
            null
        )
    }

    fun setSelectedFlight(selectedFlight: FlightDataItem) {
        this.selectedFlight = selectedFlight
    }

    fun zoomAtSelectedPlane() {
        val selectedFlightLoc =
            selectedFlight?.geography?.latitude?.toDouble()?.let {
                selectedFlight?.geography?.longitude?.toDouble()
                    ?.let { longitude -> LatLng(it, longitude) }
            }
        val zoomLevel = 8f
        selectedFlightLoc?.let {
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(selectedFlightLoc, zoomLevel),
                1500,
                null
            )
        }
    }

    fun moveCamera() {
        if (lat == null || lon == null) return
        val currentLatLng = LatLng(lat!!,lon!!)
        val cameraPosition = CameraPosition.Builder()
            .target(currentLatLng)
            .zoom(8f)
            .bearing(0f)
            .build()

        mMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition),
            1400,
            object : GoogleMap.CancelableCallback {
                override fun onFinish() {}
                override fun onCancel() {}
            }
        )
    }

    fun getVisibleBounds(): LatLngBounds? {
        return mMap?.projection?.visibleRegion?.latLngBounds
    }

    fun addPlaneMarker(
        latitude: Double,
        longitude: Double,
        markerIcon: BitmapDescriptor?,
        direction: Double?,
        flightData: FlightDataItem? = null,
        airplaneSelectedIcon: BitmapDescriptor?
    ) {
        val id = flightData?.flight?.iataNumber ?: return
        val planePosition = LatLng(latitude, longitude)

        withMapOnMain { map ->
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
            if (marker != null) {
                planeMarker[id] = marker
            }

            removeDefaultPlane(lastSelectedPlane)
            addSelectedPlane(lastSelectedPlane, airplaneSelectedIcon, lastArrivalLongLat)
        }

    }

    private val planeMarker = mutableMapOf<String, Marker>()

    fun getPlaneMarkers(): MutableMap<String, Marker> = planeMarker
}