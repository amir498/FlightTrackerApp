package com.example.flighttrackerappnew.presentation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.db.FavFlightDao
import com.example.flighttrackerappnew.data.db.FollowLiveFlightDao
import com.example.flighttrackerappnew.data.model.FollowFlightData
import com.example.flighttrackerappnew.data.model.flight.FlightDataItem
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.databinding.ActivityDetailBinding
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.adManager.banner.BannerAdManager
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.sealedClasses.Resource
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.favData
import com.example.flighttrackerappnew.presentation.utils.formatIsoDate
import com.example.flighttrackerappnew.presentation.utils.formatTo12HourTime
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.gone
import com.example.flighttrackerappnew.presentation.utils.isComeFromFav
import com.example.flighttrackerappnew.presentation.utils.isComeFromTracked
import com.example.flighttrackerappnew.presentation.utils.isFromDetail
import com.example.flighttrackerappnew.presentation.utils.lastSelectedPlane
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.example.flighttrackerappnew.presentation.utils.trackData
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class DetailActivity : BaseActivity<ActivityDetailBinding>(ActivityDetailBinding::inflate) {

    private val favFlightDao: FavFlightDao by inject()
    private val followLiveFlightDao: FollowLiveFlightDao by inject()
    private val bannerAdManager: BannerAdManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.selectedMove.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.selectedMove.layoutParams = params

        if (isComeFromTracked) {
            binding.tvFollow.text =
                ContextCompat.getString(this@DetailActivity, R.string.unfollow)
        } else {
            binding.tvFollow.text =
                ContextCompat.getString(this@DetailActivity, R.string.follow)
            getTrackedData()
        }

        setData()
        viewListener()
        observerLiveData()

        if (isComeFromFav) {
            binding.favFlightBtn.setImageResource(R.drawable.iv_fav_s)
            binding.favFlightBtn.tag = "fav"
        }

        val BANNER_DETAIL =
            RemoteConfigManager.getBoolean("BANNER_DETAIL")
        if (BANNER_DETAIL && !config.isPremiumUser) {
            binding.adContainerView.visible()
            bannerAdManager.loadAd(true, this, app.getString(R.string.BANNER_DETAIL), {
                bannerAdManager.showBannerAd(
                    binding.adContainerView,
                    this@DetailActivity,
                    null
                )
            }, {})
        }
    }

    private fun getTrackedData() {
        trackData = FollowFlightData(
            id = 0,
            depTime = FullDetailsFlightData?.scheduledDepTime,
            arrivalTime = FullDetailsFlightData?.scheduledArrTime,
            depCityName = FullDetailsFlightData?.depCity,
            arrivalCityName = FullDetailsFlightData?.arrCity,
            arrivalIataCode = FullDetailsFlightData?.arrIataCode,
            depIataCode = FullDetailsFlightData?.depIataCode,
            speedValue = FullDetailsFlightData?.vSpeed,
            altitudeValue = FullDetailsFlightData?.altitude,
            airlineName = FullDetailsFlightData?.airlineName,
            callSign = FullDetailsFlightData?.callSign,
            flightNum = FullDetailsFlightData?.flightNo,
            airCraftIataNumber = FullDetailsFlightData?.airPlaneIataCode,
            time = FullDetailsFlightData?.actualDepTime.toString(),
            30
        )
    }

    private val viewModel: FlightAppViewModel by inject()

    private var favFlightDta: List<FullDetailFlightData>? = null

    private fun observerLiveData() {
        viewModel.apply {
            viewModel.favFlightData.observe(this@DetailActivity) {
                favFlightDta = it
                if (favFlightDta != null) {
                    if (favFlightDta?.any { it.flightNo == FullDetailsFlightData?.flightNo } == true) {
                        binding.favFlightBtn.setImageResource(R.drawable.iv_fav_s)
                        binding.favFlightBtn.tag = "fav"
                        favData = FullDetailsFlightData
                    }
                }
            }
            viewModel.followFlightData.observe(this@DetailActivity) {
                val isTrackData = it?.any {
                    it.flightNum == FullDetailsFlightData?.flightNo
                }
                if (isTrackData == true) {
                    binding.tvFollow.text =
                        ContextCompat.getString(this@DetailActivity, R.string.unfollow)
                } else {
                    binding.tvFollow.text =
                        ContextCompat.getString(this@DetailActivity, R.string.follow)
                }
            }
            liveFlightData.observe(this@DetailActivity) { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val flight: List<FlightDataItem> = result.data

                        val flightData: List<FlightDataItem> = flight.filter {
                            it.flight?.iataNumber == FullDetailsFlightData?.flightNo && it.status == "en-route"
                        }
                        try {
                            lastSelectedPlane = flightData[0]
                        } catch (_: Exception) {
                        }
                    }

                    is Resource.Error -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isFromDetail = false
    }

    private fun showDialogPremium() {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_premium_without_ads)
            .setCancelable(true)
            .setPositiveClickListener {
                val intent = Intent(this, PremiumActivity::class.java)
                intent.putExtra("from_detail", true)
                startActivity(intent)
                it.dismiss()
                isFromDetail = true
            }.setCrossBtnListener {
                it.dismiss()
            }
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun viewListener() {
        binding.apply {
            conRoute.setOnClickListener {
                if (config.isPremiumUser) {
                    isFromDetail = true
                    startActivity(
                        Intent(
                            this@DetailActivity,
                            LiveMapFlightTrackerActivity::class.java
                        )
                    )
                } else {
                    showDialogPremium()
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
                binding.apply {
                    if (favFlightBtn.tag == "") {
                        if (favData != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val job = lifecycleScope.launch(Dispatchers.IO) {
                                    FullDetailsFlightData?.let { entity ->
                                        favFlightDao.insertFavFlightData(
                                            entity
                                        )
                                    }
                                }
                                job.join()
                                favFlightBtn.tag = "fav"
                                favFlightBtn.setImageResource(R.drawable.iv_fav_s)
                                this@DetailActivity.showToast("Flight added to Favorites")
                                getFavFlight()
                            }
                        }

                        lifecycleScope.launch(Dispatchers.IO) {
                            val job = lifecycleScope.launch(Dispatchers.IO) {
                                FullDetailsFlightData?.let { entity ->
                                    favFlightDao.insertFavFlightData(
                                        entity
                                    )
                                }
                            }
                            job.join()
                            favFlightBtn.tag = "fav"
                            favFlightBtn.setImageResource(R.drawable.iv_fav_s)
                            this@DetailActivity.showToast("Flight added to Favorites")
                            getFavFlight()
                        }

                    } else {
                        if (favData != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                val job = lifecycleScope.launch(Dispatchers.IO) {
                                    favData!!.flightNo.let { flightNumber ->
                                        favFlightDao.deleteFavFlightByNumber(
                                            flightNumber
                                        )
                                    }
                                }
                                job.join()
                                favFlightBtn.tag = ""
                                favFlightBtn.setImageResource(R.drawable.iv_fav)
                                this@DetailActivity.showToast("Flight removed from Favorites")
                                getFavFlight()
                            }
                        }
                        lifecycleScope.launch(Dispatchers.IO) {
                            val job = lifecycleScope.launch(Dispatchers.IO) {
                                FullDetailsFlightData?.let { entity ->
                                    favFlightDao.deleteFavFlightByNumber(
                                        entity.flightNo
                                    )
                                }
                            }
                            job.join()
                            favFlightBtn.tag = ""
                            favFlightBtn.setImageResource(R.drawable.iv_fav)
                            this@DetailActivity.showToast("Flight removed from Favorites")
                            getFavFlight()
                        }

                    }
                }
            }
            consFollow.setOnClickListener {
                if (binding.tvFollow.text == ContextCompat.getString(
                        this@DetailActivity,
                        R.string.unfollow
                    )
                ) {
                    this@DetailActivity.showToast("Flight is not being Followed")
                    binding.tvFollow.text =
                        ContextCompat.getString(this@DetailActivity, R.string.follow)

                    if (trackData != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val job = lifecycleScope.launch(Dispatchers.IO) {
                                trackData!!.flightNum.let { flightNumber ->
                                    flightNumber?.let {
                                        followLiveFlightDao.deleteFollowFlightByNumber(
                                            it
                                        )
                                    }
                                }
                            }
                            job.join()
                            viewModel.getFollowFlightData()
                        }
                    }
                } else {
                    this@DetailActivity.showToast("Flight is being Followed")
                    binding.tvFollow.text = ContextCompat.getString(
                        this@DetailActivity,
                        R.string.unfollow
                    )
                    if (trackData != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val job = lifecycleScope.launch(Dispatchers.IO) {
                                trackData!!.flightNum.let { flightNumber ->
                                    flightNumber?.let {
                                        followLiveFlightDao.insertFollowLiveFlightData(
                                            trackData!!
                                        )
                                    }
                                }
                            }
                            job.join()
                            viewModel.getFollowFlightData()
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

    fun setData() {
        binding.apply {
            favFlightBtn.tag = ""
            depIataCode.text = FullDetailsFlightData?.depIataCode
            arrivalIataCode.text = FullDetailsFlightData?.arrIataCode
            depCityName.text = FullDetailsFlightData?.depCity
            arrCityName.text = FullDetailsFlightData?.arrCity
            tvAmericanAirlines.text = FullDetailsFlightData?.airlineName
            depTime.text = FullDetailsFlightData?.scheduledDepTime
            arriTime.text = FullDetailsFlightData?.scheduledArrTime
            depActualTime.text = FullDetailsFlightData?.scheduledDepTime
            arrEstimatedTime.text = FullDetailsFlightData?.scheduledArrTime
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
            discreteSeekBar.progress = 50
        }
    }
}