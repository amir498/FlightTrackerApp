package com.example.flighttrackerappnew.presentation.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.db.FollowLiveFlightDao
import com.example.flighttrackerappnew.data.model.follow.FollowFlightData
import com.example.flighttrackerappnew.databinding.ActivityFollowedFlightBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.DetailActivityForSearch
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.adapter.FollowFlightAdapter
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_TRACKED_FLIGHT
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.listener.FollowedFlightListener
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.toFullDetail
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FollowedFlightActivity : BaseActivity<ActivityFollowedFlightBinding>(ActivityFollowedFlightBinding::inflate),FollowedFlightListener{
    private val viewModel: FlightAppViewModel by inject()
    private val followLiveFlightDao: FollowLiveFlightDao by inject()
    private val adapter = FollowFlightAdapter()
    private val rewardedAd: RewardedAdManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewListener()
    }

    private fun viewListener() {
        binding.apply {
            btnBack.setOnClickListener {
               finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.flAdplaceholder.invisible()
        getFollowedFlight()
    }

    private fun getFollowedFlight() {
        viewModel.getFollowFlightData()
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
        binding.recyclerView.adapter = adapter
        adapter.apply {
            setList(data)
            setListener(this@FollowedFlightActivity)
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
            val intent = Intent(this, PremiumActivity2::class.java)
            intent.putExtra("from_arrival", true)
            startActivity(intent)
        } else {
            val intent = Intent(this, PremiumActivity::class.java)
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
                    startActivity(Intent(this, DetailActivityForSearch::class.java))
                }, {
                    startActivity(Intent(this, DetailActivityForSearch::class.java))
                }
            )
        } else {
            startActivity(Intent(this, DetailActivityForSearch::class.java))
        }
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
                        followLiveFlightDao.deleteFollowFlightByNumber(data.flightNo)
                    }
                    job.join()
                    viewModel.getFollowFlightData()
                }
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    override fun onViewDetailedClicked(data: FollowFlightData) {
        FullDetailsFlightData = data.toFullDetail()
        if (config.isPremiumUser) {
            startActivity(Intent(this, DetailActivityForSearch::class.java))
        } else {
            showDialogPremium()
        }
    }
}