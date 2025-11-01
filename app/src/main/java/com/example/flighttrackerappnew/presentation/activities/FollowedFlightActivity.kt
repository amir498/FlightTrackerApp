package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.db.FollowLiveFlightDao
import com.example.flighttrackerappnew.data.model.FollowFlightData
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.databinding.ActivityFollowedFlightBinding
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.adapter.FollowFlightAdapter
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_TRACKED_FLIGHT
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.listener.FollowedFlightListener
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isComeFromFollowed
import com.example.flighttrackerappnew.presentation.utils.trackData
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FollowedFlightActivity : BaseActivity<ActivityFollowedFlightBinding>(ActivityFollowedFlightBinding::inflate),
    FollowedFlightListener {

    private val followLiveFlightDao: FollowLiveFlightDao by inject()
    private val adapter = FollowFlightAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        initView()
        getFollowedFlight()
        viewListener()
        isComeFromFollowed = true
        viewModel.getFollowFlightData()
    }

    private val viewModel: FlightAppViewModel by inject()
    private fun getFollowedFlight() {
        viewModel.followFlightData.observe(this) { flightList ->
            setData(flightList)
            if (flightList.isEmpty()) {
                binding.conPlaceholder.visible()
                binding.flAdplaceholder.invisible()
            } else {
                loadAd()
            }
        }
    }

    private fun loadAd() {
        NATIVE_TRACKED_FLIGHT.apply {
            loadNativeAd(
                this@FollowedFlightActivity,
                RemoteConfigManager.getBoolean("NATIVE_TRACKED_FLIGHT")
            )
            showNativeAd(
                adGroup = NATIVE_TRACKED_FLIGHT,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_layout_view_with_media,
               activity =  this@FollowedFlightActivity
            )
        }
    }

    private fun setData(data: List<FollowFlightData>) {
        adapter.apply {
            setList(data)
            setListener(this@FollowedFlightActivity)
        }
    }

    private fun viewListener() {
        binding.apply {
            btnBack.setOnClickListener {
                this@FollowedFlightActivity.finish()
            }
        }
    }

    private fun initView() {
        binding.recyclerView.adapter = adapter
    }

    override fun onUnFollowClicked(data: FollowFlightData) {
        showUnFollowDialog(data)
    }

    private fun showUnFollowDialog(data: FollowFlightData) {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_unfav)
            .setCancelable(false)
            .setPositiveClickListener {
                it.dismiss()
                lifecycleScope.launch(Dispatchers.IO) {
                    val job = lifecycleScope.launch(Dispatchers.IO) {
                        followLiveFlightDao.deleteFollowFlightByNumber(data.flightNum.toString())
                    }
                    job.join()
                    viewModel.getFollowFlightData()
                }
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    private val rewardedAd: RewardedAdManager by inject()

    override fun onViewDetailedClicked(data: FollowFlightData) {
        trackData = data
        FullDetailsFlightData = FullDetailFlightData(
            0,
            trackData?.flightNum.toString(),
            depIataCode = trackData?.depIataCode.toString(),
            arrIataCode = trackData?.arrivalIataCode.toString(),
            arrAirportName = "",
            depAirportName = "",
            arrCity = trackData?.arrivalCityName.toString(),
            depCity = trackData?.depCityName.toString(),
            nameAirport = "",
            callSign = trackData?.callSign.toString(),
            scheduled = "",
            actualDepTime = "",
            gate = "",
            scheduledDepTime = "",
            scheduledArrTime = "",
            estimatedArrTime = "",
            flightIataNumber = trackData?.flightNum.toString(),
            flightIcaoNo = trackData?.depIataCode.toString(),
            airlineName = trackData?.airlineName.toString(),
            terminal = "",
            modelCode = "",
            status = "",
            vSpeed = "",
            hSpeed = "",
            longitude = "",
            latitude = "",
            direction = "",
            altitude = "",
            delay = "",
            currentOwner = "",
            planeStatus = "",
            airCraftType = "",
            airPlaneIataCode = "",
            airLineIataCode = "",
            airLineICaoCode = "",
            lineNumber = "",
            series = "",
            productionLine = "",
            icaoHex = "",
            iataModel = "",
            regNo = "",
            modelName = "",
            squawk = "",
            constructionNo = "",
            firstFlight = "",
            deliveryDate = "",
            rolloutDate = "",
            engineCount = "",
            regDate = "",
            progress = 0
        )
        if (config.isPremiumUser) {
            startActivity(Intent(this, DetailActivity::class.java))
        } else {
            showDialogPremium()
        }
    }

    private fun showDialogPremium() {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_premium)
            .setCancelable(false)
            .setPositiveClickListener {
                showPremiumScreen()
                it.dismiss()
            }.setNegativeClickListener {
                showRewardedAd()
                it.dismiss()
            }.show()
    }

    private fun showPremiumScreen() {
        config.startDiscountIfNeeded()

        val isDiscountActive = config.isDiscountActive()

        if (isDiscountActive) {
            val intent = Intent(this@FollowedFlightActivity, PremiumActivity2::class.java)
            intent.putExtra("from_arrival", true)
            startActivity(intent)
        } else {
            val intent = Intent(this@FollowedFlightActivity, PremiumActivity::class.java)
            intent.putExtra("from_arrival", true)
            startActivity(intent)
        }
    }

    private fun showRewardedAd() {
        val REWARDED_FOLLOW =
            RemoteConfigManager.getBoolean("REWARDED_FOLLOW")
        if (REWARDED_FOLLOW) {
            rewardedAd.loadAndShowRewardedAd(
                this,
                app.getString(R.string.REWARDED_ARRIVAL),
                onRewardEarned = {
                    startActivity(Intent(this, DetailActivity::class.java))
                }, {
                    startActivity(Intent(this, DetailActivity::class.java))
                }
            )
        } else {
            startActivity(Intent(this, DetailActivity::class.java))
        }
    }
}