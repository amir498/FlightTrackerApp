package com.example.flighttrackerappnew.presentation.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.nearby.NearByAirportsDataItems
import com.example.flighttrackerappnew.databinding.ActivityNearByBinding
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_NEARBy_AIRPORT
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.googleMap.MyGoogleMapNearAirports
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.getCurrentCountryLatLon
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.lat
import com.example.flighttrackerappnew.presentation.utils.lon
import com.example.flighttrackerappnew.presentation.utils.openGoogleMap
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class NearByActivity : BaseActivity<ActivityNearByBinding>(ActivityNearByBinding::inflate) {

    private val viewModel: FlightAppViewModel by inject()
    private lateinit var mBottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>
    private val googleMap: MyGoogleMapNearAirports by inject()
    private var nearbyAirports = ArrayList<NearByAirportsDataItems>()
    private var drawMarkersJob: Job? = null
    private var markerIcon: BitmapDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getBottomSheetReference()
        bottomSheetListener()
        initView()
        getMarkerIcon()
        viewListener()
        observeLiveData()
    }

    private fun viewListener() {
        binding.apply {
            backBtn.setOnClickListener {
                this@NearByActivity.finish()
            }
            currentLocationBtn.setOnClickListener {
                googleMap.moveCamera()
            }
        }
    }

    private fun getMarkerIcon() {
        val drawable = ContextCompat.getDrawable(this, R.drawable.iv_nearby_airport) ?: return

        val width = 50
        val height = 50

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)

        markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        try {
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.google_map_fragment) as SupportMapFragment
            googleMap.apply {
                setMapUi(mapFragment)
                listener { nearAirport ->
                    try {
                        binding.include.btnGetDirection.setOnClickListener {
                            openGoogleMap(
                                nearAirport.latitudeAirport.toString(),
                                nearAirport.longitudeAirport.toString(),
                                this@NearByActivity
                            )
                        }
                        binding.include.distance.text =
                            "${nearAirport.distance.toString().take(5)} km"
                        binding.include.airportIataCode.text = nearAirport.codeIataAirport
                        binding.include.airportName.text = nearAirport.nameAirport
                        binding.include.countryName.text = nearAirport.nameCountry
                        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun bottomSheetListener() {
        mBottomSheetBehaviour.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {}

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {}

                    BottomSheetBehavior.STATE_DRAGGING -> {}

                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {}

                    BottomSheetBehavior.STATE_SETTLING -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun getBottomSheetReference() {
        val bottomSheetParent = binding.include.root
        mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetParent)
        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun loadBannerAd() {
        BANNER_NEARBy_AIRPORT.apply {
            loadAndShowBannerAd(
                context = this@NearByActivity,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {})
        }
    }

    private var dialog: Dialog? = null

    private fun showDialog() {
        if (dialog == null) {
            dialog = CustomDialogBuilder(this).setLayout(R.layout.dialog_retry_bearby_airport).setCancelable(false)
                .setPositiveClickListener {
                    binding.pg.visible()
                    it.dismiss()
                    getNearbyAirport()
                    dialog = null
                }.setNegativeClickListener {
                    it.dismiss()
                    dialog = null
                }.show()
        }
    }

    private fun getNearbyAirport() {
        val pair = getCurrentCountryLatLon(this)
        lat = pair?.first
        lon = pair?.second
        lat?.let { lat ->
            lon?.let { lon ->
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getNearBy(
                        lat, lon, RemoteConfigManager.getString("distance").toInt()
                    )
                }
            }
        }
    }

    private suspend fun setAirportsData(currentJob: Job) {
        val visibleBounds = withContext(Dispatchers.Main) {
            googleMap.getVisibleBounds()
        }

        val visibleAirports = withContext(Dispatchers.Default) {
            currentJob.ensureActive()
            nearbyAirports.asSequence().filter { airport ->
                currentJob.ensureActive()
                val lat = airport.latitudeAirport
                val lng = airport.longitudeAirport
                lat != null && lng != null && visibleBounds?.contains(LatLng(lat, lng)) == true
            }.toList()
        }

        val visibleAirportIds = visibleAirports.mapNotNull { it.codeIataAirport }.toSet()

        withContext(Dispatchers.Main) {
            synchronized(lock) {
                val iterator = googleMap.airportMarkers.entries.iterator()
                while (iterator.hasNext()) {
                    val (iataCode, marker) = iterator.next()
                    if (iataCode !in visibleAirportIds) {
                        marker.remove()
                        iterator.remove()
                    }
                }
            }

            visibleAirports.forEach { airport ->
                currentJob.ensureActive()
                val lat = airport.latitudeAirport ?: return@forEach
                val lng = airport.longitudeAirport ?: return@forEach
                val code = airport.codeIataAirport ?: return@forEach

                if (!googleMap.airportMarkers.containsKey(code)) {
                    googleMap.addAirportMarker(lat, lng, markerIcon, null, airport)
                }
            }
        }
    }
    private val lock = Any()
    private fun observeLiveData() {
        viewModel.apply {
            nearByData.observe(this@NearByActivity) { result ->
                when (result) {
                    is Resource.Loading -> {
                        binding.pg.visible()
                    }

                    is Resource.Success -> {
                        try {
                            nearbyAirports = result.data as ArrayList<NearByAirportsDataItems>
                            if (nearbyAirports.isNotEmpty()) {
                                loadBannerAd()
                            }
                            setLiveFlightData()
                        } catch (_: NullPointerException) {
                            this@NearByActivity.showToast("No Data found")
                        }
                    }

                    is Resource.Error -> {
                        binding.pg.invisible()
                        showDialog()
                        this@NearByActivity.showToast(result.message)
                    }
                }
            }
        }
    }

    fun setLiveFlightData() {
        Handler(Looper.getMainLooper()).postDelayed({
            googleMap.moveCameraToCurrentLocation()
            googleMap.onCameraIdle { newVisibleBounds ->
                binding.pg.visible()
                drawMarkersJob?.cancel()
                drawMarkersJob = lifecycleScope.launch {
                    setAirportsData(coroutineContext[Job]!!)
                    binding.pg.invisible()
                }
            }
        }, 2000)

        googleMap.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                drawMarkersJob?.cancel()
            }
        }
    }
}