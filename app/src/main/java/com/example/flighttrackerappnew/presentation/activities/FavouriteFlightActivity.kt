package com.example.flighttrackerappnew.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.db.FavFlightDao
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.databinding.ActivityFavouriteFlightBinding
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.adapter.FavFlightAdapter
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.listener.FavFlightListener
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.favData
import com.example.flighttrackerappnew.presentation.utils.getStatusBarHeight
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isComeFromFav
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

class FavouriteFlightActivity :
    BaseActivity<ActivityFavouriteFlightBinding>(ActivityFavouriteFlightBinding::inflate),
    FavFlightListener {

    private val favFlightDao: FavFlightDao by inject()
    private val viewModel: FlightAppViewModel by inject()

    private val adapter = FavFlightAdapter()
    private val nativeAdController: NativeAdController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val params = binding.btnBack.layoutParams as ConstraintLayout.LayoutParams
        params.topMargin = getStatusBarHeight
        binding.btnBack.layoutParams = params

        initView()
        getFavFlight()
        viewListener()
    }

    private fun getFavFlight() {
        lifecycleScope.launch {
            viewModel.getFavFlightData()
            viewModel.favFlightData.observe(this@FavouriteFlightActivity) { flightList ->
                setData(flightList)
                if (flightList.isEmpty()) {
                    binding.conFav.visible()
                    binding.flAdplaceholder.invisible()
                } else {
                    binding.conFav.invisible()
                    binding.flAdplaceholder.visible()
                    val NATIVE_SAVED_FLIGHT =
                        RemoteConfigManager.getBoolean("NATIVE_SAVED_FLIGHT")
                    if (NATIVE_SAVED_FLIGHT) {
                        binding.flAdplaceholder.visible()
                        nativeAdController.apply {
                            loadNativeAd(
                                this@FavouriteFlightActivity,
                                app.getString(R.string.NATIVE_SAVED_FLIGHT)
                            )
                            showNativeAd(
                                this@FavouriteFlightActivity,
                                binding.flAdplaceholder
                            )
                        }
                    }

                }
            }
        }
    }

    private fun setData(data: List<FullDetailFlightData>) {
        adapter.apply {
            setList(data)
            setListener(this@FavouriteFlightActivity)
        }
    }

    private fun viewListener() {
        binding.apply {
            btnBack.setOnClickListener {
                this@FavouriteFlightActivity.finish()
            }
        }
    }

    private fun initView() {
        binding.recyclerView.adapter = adapter
    }

    override fun onUnFavClicked(data: FullDetailFlightData) {
        showUnFavDialog(data)
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
                    getFavFlight()
                }
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    override fun onViewDetailedClicked(data: FullDetailFlightData) {
        favData = data
        isComeFromFav = true
        showRewardedAd()
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