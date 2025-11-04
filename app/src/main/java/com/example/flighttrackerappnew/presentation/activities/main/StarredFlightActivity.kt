package com.example.flighttrackerappnew.presentation.activities.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.db.StarredFlightDao
import com.example.flighttrackerappnew.data.model.fav.FavFlightData
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.databinding.ActivityStarredFlightBinding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.DetailActivityForSearch
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.adapter.FavFlightAdapter
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_SAVED_FLIGHT
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.listener.FavFlightListener
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.toFullDetailData
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class StarredFlightActivity :
    BaseActivity<ActivityStarredFlightBinding>(ActivityStarredFlightBinding::inflate),
    FavFlightListener {
    private val viewModel: FlightAppViewModel by inject()
    private val adapter = FavFlightAdapter()
    private val favFlightDao: StarredFlightDao by inject()

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
        getFavFlight()
    }

    private fun getFavFlight() {
        viewModel.getFavFlightData()
        viewModel.favFlightData.observe(this@StarredFlightActivity) { flightList ->
            setData(flightList)
            if (flightList.isEmpty()) {
                binding.conFav.visible()
                binding.flAdplaceholder.invisible()
            } else {
                binding.conFav.invisible()
                loadAd()
            }
        }
    }

    private fun setData(data: List<FavFlightData>) {
        binding.recyclerView.adapter = adapter
        adapter.apply {
            setList(data)
            setListener(this@StarredFlightActivity)
        }
    }

    private fun loadAd() {
        NATIVE_SAVED_FLIGHT.apply {
            loadNativeAd(
                this@StarredFlightActivity,
                RemoteConfigManager.getBoolean("NATIVE_SAVED_FLIGHT")
            )
            showNativeAd(
                adGroup = NATIVE_SAVED_FLIGHT,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_layout_view_with_media,
                activity = this@StarredFlightActivity
            )
        }
    }

    override fun onUnFavClicked(data: FavFlightData) {
        showUnFavDialog(data.toFullDetailData())
    }

    private fun showUnFavDialog(data: FullDetailFlightData) {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_unfav)
            .setCancelable(false)
            .setPositiveClickListener {
                it.dismiss()
                lifecycleScope.launch(Dispatchers.IO) {
                    val job = lifecycleScope.launch(Dispatchers.IO) {
                        favFlightDao.deleteFavFlightByNumber(data.flightNo)
                    }
                    job.join()
                    viewModel.getFavFlightData()
                }
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    override fun onViewDetailedClicked(data: FavFlightData) {
        FullDetailsFlightData = data.toFullDetailData()
        if (config.isPremiumUser) {
            startActivity(Intent(this, DetailActivityForSearch::class.java))
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
            }.setCrossBtnListener {
                it.dismiss()
            }
            .show()
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

    private val rewardedAd: RewardedAdManager by inject()
    private fun showRewardedAd() {

        val REWARDED_FAV =
            RemoteConfigManager.getBoolean("REWARDED_FAV")
        if (REWARDED_FAV) {
            rewardedAd.loadAndShowRewardedAd(
                this,
                app.getString(R.string.REWARDED_FAV),
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
}