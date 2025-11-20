package com.example.flighttrackerappnew.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.db.FollowLiveFlightDao
import com.example.flighttrackerappnew.data.db.StarredFlightDao
import com.example.flighttrackerappnew.data.model.airport.AirportsDataItems
import com.example.flighttrackerappnew.data.model.fav.FavFlightData
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.follow.FollowFlightData
import com.example.flighttrackerappnew.databinding.ActivityDetailBinding
import com.example.flighttrackerappnew.presentation.activities.main.LiveMapDetailRouteActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2
import com.example.flighttrackerappnew.presentation.admob.banner.BannerAdProvider.BANNER_DETAIL
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.formatIsoDate
import com.example.flighttrackerappnew.presentation.utils.formatTo12HourTime
import com.example.flighttrackerappnew.presentation.utils.getTimeDifference
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.logDebug
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.toFavFlightData
import com.example.flighttrackerappnew.presentation.utils.toFollowFlightData
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class DetailActivityForSearch :
    BaseActivity<ActivityDetailBinding>(ActivityDetailBinding::inflate) {
    private val viewModel: FlightAppViewModel by inject()
    private var favFlightDtaList: List<FavFlightData>? = null
    private var followFlightDtaList: List<FollowFlightData>? = null
    private val favFlightDao: StarredFlightDao by inject()
    private val followLiveFlightDao: FollowLiveFlightDao by inject()
    private var isFavFlight = false
    private var isFollowFlight = false
    var flights: List<FlightDataItem> = emptyList()
    private var airportsDataList: List<AirportsDataItems>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observerData()
        setData()
        loadBannerAd()
        getFavFlight()
        getFollowFlight()
        viewListener()
    }

    private fun observerData() {
        viewModel.apply {
            airPortsData.observe(this@DetailActivityForSearch) { result ->
                when (result) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        airportsDataList = result.data
                    }

                    is Resource.Error -> {
                    }
                }
            }
            liveFlightData.observe(this@DetailActivityForSearch) { result ->
                when (result) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        flights = result.data
                    }

                    is Resource.Error -> {
                    }
                }
            }

            followFlightData.observe(this@DetailActivityForSearch) { followList ->
                followFlightDtaList = followList
                if (followFlightDtaList?.any { it.flightNo == FullDetailsFlightData?.flightNo } == true) {
                    binding.tvFollow.text =
                        ContextCompat.getString(this@DetailActivityForSearch, R.string.unfollow)
                    isFollowFlight = true
                } else {
                    binding.tvFollow.text =
                        ContextCompat.getString(this@DetailActivityForSearch, R.string.follow)
                    isFollowFlight = false
                }
                binding.pg.invisible()
            }
            favFlightData.observe(this@DetailActivityForSearch) { favList ->
                favFlightDtaList = favList
                if (favFlightDtaList?.any { it.flightNo == FullDetailsFlightData?.flightNo } == true) {
                    binding.favFlightBtn.setImageResource(R.drawable.iv_fav_s)
                    isFavFlight = true
                } else {
                    binding.favFlightBtn.setImageResource(R.drawable.iv_fav)
                    isFavFlight = false

                }
                binding.pg.invisible()
            }
        }
    }

    private fun showPremiumScreen() {
        config.startDiscountIfNeeded()

        val isDiscountActive = config.isDiscountActive()

        if (isDiscountActive) {
            val intent = Intent(this@DetailActivityForSearch, PremiumActivity2::class.java)
            intent.putExtra("from_detail", true)

            startActivity(intent)
        } else {
            val intent = Intent(this@DetailActivityForSearch, PremiumActivity::class.java)
            intent.putExtra("from_detail", true)
            startActivity(intent)
        }
    }

    private fun showDialogPremium() {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_premium_without_ads)
            .setCancelable(true)
            .setPositiveClickListener {
                showPremiumScreen()
                it.dismiss()
            }.setCrossBtnListener {
                it.dismiss()
            }
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun viewListener() {
        binding.apply {
            conRoute.setOnClickListener {
                val selectedFlight =
                    flights.filter { FullDetailsFlightData?.flightNo == it.flight?.iataNumber }[0]
                val depAirport =
                    airportsDataList?.filter { it.codeIataAirport == selectedFlight?.departure?.iataCode } as ArrayList<AirportsDataItems>?
                val arvAirport =
                    airportsDataList?.filter { it.codeIataAirport == selectedFlight?.arrival?.iataCode } as ArrayList<AirportsDataItems>?

                if (depAirport != null && arvAirport != null) {
                    if (config.isPremiumUser) {
                        startActivity(
                            Intent(
                                this@DetailActivityForSearch,
                                LiveMapDetailRouteActivity::class.java
                            )
                        )
                    } else {
                        showDialogPremium()
                    }
                } else {
                    this@DetailActivityForSearch.showToast("No Route Data Found For This Flight")
                }
            }
            discreteSeekBar.setOnTouchListener { _, _ -> true }
            btnArrowUp.setOnClickListener {
                if (btnArrowUp.tag == "close") {
                    btnArrowUp.tag = "open"
                    conAirCraft.visible()
                    btnArrowUp.rotation = 180f
                } else {
                    btnArrowUp.tag = "close"
                    conAirCraft.gone()
                    btnArrowUp.rotation = 0f
                }
            }
            btnArrowUpM.setOnClickListener {
                if (btnArrowUpM.tag == "close") {
                    btnArrowUpM.tag = "open"
                    conManufacture.visible()
                    btnArrowUpM.rotation = 180f
                } else {
                    btnArrowUpM.tag = "close"
                    conManufacture.gone()
                    btnArrowUpM.rotation = 0f
                }
            }
            btnArrowUpEngineDetails.setOnClickListener {
                if (btnArrowUpEngineDetails.tag == "close") {
                    btnArrowUpEngineDetails.tag = "open"
                    conEngineDetails.visible()
                    btnArrowUpEngineDetails.rotation = 180f
                } else {
                    btnArrowUpEngineDetails.tag = "close"
                    conEngineDetails.gone()
                    btnArrowUpEngineDetails.rotation = 0f
                }
            }
            btnArrowUpImportantDate.setOnClickListener {
                if (btnArrowUpImportantDate.tag == "close") {
                    btnArrowUpImportantDate.tag = "open"
                    conImportantDate.visible()
                    btnArrowUpImportantDate.rotation = 180f
                } else {
                    btnArrowUpImportantDate.tag = "close"
                    conImportantDate.gone()
                    btnArrowUpImportantDate.rotation = 0f
                }
            }
            favFlightBtn.setOnClickListener {
                binding.pg.visible()
                lifecycleScope.launch(Dispatchers.IO) {
                    FullDetailsFlightData?.let { fullDetails ->
                        val favData = fullDetails.toFavFlightData()
                        if (isFavFlight) {
                            this@DetailActivityForSearch.showToast("Remove from Fav")
                            favFlightDao.deleteFavFlightByNumber(
                                favData.flightNo
                            )
                        } else {
                            this@DetailActivityForSearch.showToast("Add to Fav")
                            favFlightDao.insertFavFlightData(
                                favData
                            )
                        }
                        withContext(Dispatchers.Main) {
                            getFavFlight()
                        }
                    }
                }
            }
            consFollow.setOnClickListener {
                binding.pg.visible()
                lifecycleScope.launch(Dispatchers.IO) {
                    FullDetailsFlightData?.let { fullDetails ->
                        val favData = fullDetails.toFollowFlightData()
                        if (isFollowFlight) {
                            this@DetailActivityForSearch.showToast("You UnFollowed this Flight")
                            followLiveFlightDao.deleteFollowFlightByNumber(
                                favData.flightNo
                            )
                        } else {
                            this@DetailActivityForSearch.showToast("Flight is Being Followed")
                            followLiveFlightDao.insertFollowLiveFlightData(
                                favData
                            )
                        }
                        withContext(Dispatchers.Main) {
                            getFollowFlight()
                        }
                    }
                }
            }
            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun getFavFlight() {
        lifecycleScope.launch {
            viewModel.getFavFlightData()
        }
    }

    private fun getFollowFlight() {
        lifecycleScope.launch {
            viewModel.getFollowFlightData()
        }
    }

    fun setData() {
        binding.apply {
            depIataCode.text = FullDetailsFlightData?.depIataCode
            arrivalIataCode.text = FullDetailsFlightData?.arrIataCode
            depCityName.text = FullDetailsFlightData?.depCity
            arrCityName.text = FullDetailsFlightData?.arrCity
            tvAmericanAirlines.text = FullDetailsFlightData?.airlineName
            depTime.text = formatTo12HourTime(FullDetailsFlightData?.scheduledDepTime ?: "N/A")
            arriTime.text = formatTo12HourTime(FullDetailsFlightData?.scheduledArrTime ?: "N/A")
            depActualTime.text = formatTo12HourTime(FullDetailsFlightData?.actualDepTime ?: "N/A")
            arrEstimatedTime.text =
                formatTo12HourTime(FullDetailsFlightData?.estimatedArrTime ?: "N/A")
            terminalValue.text = FullDetailsFlightData?.terminal
            GateNo.text = FullDetailsFlightData?.gate
            delayValue.text = formatTo12HourTime(FullDetailsFlightData?.delay ?: "N/A")
            Scheduled.text = formatTo12HourTime(FullDetailsFlightData?.scheduled ?: "N/A")
            altitudeValue.text = FullDetailsFlightData?.altitude ?: "N/A"
            Direction.text = FullDetailsFlightData?.direction ?: "N/A"
            latitudeValue.text = FullDetailsFlightData?.latitude ?: "N/A"
            Longitude.text = FullDetailsFlightData?.longitude ?: "N/A"
            HSpeed.text = FullDetailsFlightData?.hSpeed ?: "N/A"
            Speed.text = FullDetailsFlightData?.vSpeed ?: "N/A"
            enRoute.text = FullDetailsFlightData?.status ?: "N/A"
            SquawkValue.text = FullDetailsFlightData?.squawk ?: "N/A"
            flightNum.text = FullDetailsFlightData?.flightNo
            callSign.text = FullDetailsFlightData?.callSign
            airlineName.text = FullDetailsFlightData?.nameAirport
            modelName.text = FullDetailsFlightData?.modelName ?: "N/A"
            modelCode.text = FullDetailsFlightData?.modelCode ?: "N/A"
            aircraftType.text = FullDetailsFlightData?.airCraftType ?: "N/A"
            regNo.text = FullDetailsFlightData?.regNo ?: "N/A"
            iataModel.text = FullDetailsFlightData?.iataModel ?: "N/A"
            ICAOHex.text = FullDetailsFlightData?.icaoHex ?: "N/A"
            FirstFlightDate.text =
                FullDetailsFlightData?.firstFlight?.let { formatIsoDate(it) } ?: "N/A"
            DeliveryDate.text =
                FullDetailsFlightData?.deliveryDate?.let { formatIsoDate(it) } ?: "N/A"
            RegisterationDate.text =
                FullDetailsFlightData?.regDate?.let { formatIsoDate(it) } ?: "N/A"
            rolloutDate.text =
                FullDetailsFlightData?.rolloutDate?.let { formatIsoDate(it) } ?: "N/A"
            EngineType.text = FullDetailsFlightData?.squawk ?: "N/A"
            RegisterationDates.text = FullDetailsFlightData?.regNo ?: "N/A"
            active.text = FullDetailsFlightData?.planeStatus ?: "N/A"
            EngineCount.text = FullDetailsFlightData?.engineCount ?: "N/A"
            AirCraftiataNumber.text = FullDetailsFlightData?.airPlaneIataCode ?: "N/A"
            ProductionLine.text = FullDetailsFlightData?.productionLine ?: "N/A"
            Series.text = FullDetailsFlightData?.series ?: "N/A"
            LineNo.text = FullDetailsFlightData?.lineNumber ?: "N/A"
            constructionNo.text = FullDetailsFlightData?.constructionNo ?: "N/A"
            IcaoAirLine.text = FullDetailsFlightData?.flightIcaoNo ?: "N/A"
            iataCodeAirline.text = FullDetailsFlightData?.flightIataNumber ?: "N/A"
            discreteSeekBar.progress = FullDetailsFlightData?.progress ?: 0
            val arr = formatTo12HourTime(FullDetailsFlightData?.estimatedArrTime ?: "N/A")
            time.text = getTimeDifference(arr)
        }
        when(FullDetailsFlightData?.status.toString()){
            "en-route" -> {
                binding.tvActive.text = resources.getString(R.string.active)
            }
            "unknown" -> {
                binding.tvActive.text = resources.getString(R.string.n_a)
            }
            "landed" -> {
                binding.tvActive.text = resources.getString(R.string.landed)
                binding.discreteSeekBar.progress = 100
            }
        }
        logDebug("asesa",FullDetailsFlightData?.status.toString())
    }

    private fun loadBannerAd() {
        BANNER_DETAIL.apply {
            loadAndShowBannerAd(
                context = this@DetailActivityForSearch,
                adContainerView = binding.adContainerView,
                onStartLoadingAd = {}
            )
        }
    }
}