package com.example.flighttrackerappnew.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.data.model.fulldetails.FullDetailFlightData
import com.example.flighttrackerappnew.databinding.FragmentArrivalFlightBinding
import com.example.flighttrackerappnew.presentation.activities.AirportSearchActivity
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.DetailActivityForSearch
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity
import com.example.flighttrackerappnew.presentation.activities.premium.PremiumActivity2
import com.example.flighttrackerappnew.presentation.adManager.rewarded.RewardedAdManager
import com.example.flighttrackerappnew.presentation.adapter.ArrivalFlightAdapter
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.FullDetailsFlightData
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.isFromAirportOrAirline
import com.example.flighttrackerappnew.presentation.utils.searchedDataSubTitle
import com.example.flighttrackerappnew.presentation.utils.visible
import com.example.flighttrackerappnew.presentation.viewmodels.FlightAppViewModel
import org.koin.android.ext.android.inject

class ArrivalFlightFragment : Fragment() {

    private val config: Config by inject()
    private var adapter = ArrivalFlightAdapter()
    private var arrivalData = ArrayList<FullDetailFlightData>()
    private val rewardedAd: RewardedAdManager by inject()
    private val viewModel: FlightAppViewModel by inject()

    private val binding: FragmentArrivalFlightBinding by lazy {
        FragmentArrivalFlightBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        if (isFromAirportOrAirline && !config.isPremiumUser) {
            loadAd()
        }
    }

    private fun loadAd() {
        NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline.apply {
            loadNativeAd(
                requireContext(),
                RemoteConfigManager.getBoolean("NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline")
            )
        }
    }

    fun checkData() {
        if (arrivalData.isEmpty()) {
            (activity as AirportSearchActivity).binding.AirportName.invisible()
        } else {
            (activity as AirportSearchActivity).binding.AirportName.visible()
        }
    }

    private fun observeData() {
        binding.recyclerView.adapter = adapter
        viewModel.arrivalFlightData.observe(viewLifecycleOwner) { arrivalData ->
            if (arrivalData.isEmpty()) {
                binding.conPlaceholder.visible()
                binding.pg.invisible()
                (activity as AirportSearchActivity).binding.AirportName.invisible()
            } else {
                if (!isFromAirportOrAirline && !config.isPremiumUser) {
                    loadAdForTail()
                }
                this.arrivalData = arrivalData
                var arrData = if (isFromAirportOrAirline && !config.isPremiumUser) {
                    addAdToArrivalData()
                } else {
                    arrivalData
                }
                adapter.setList(arrData)
                adapter.setListener { arrivalData ->
                    if ((requireActivity() as BaseActivity<*>).config.isPremiumUser) {
                        startActivity(Intent(requireContext(), DetailActivityForSearch::class.java))
                    } else {
                        showDialogPremium()
                    }
                    FullDetailsFlightData = arrivalData
                }

                binding.pg.invisible()
                binding.conPlaceholder.invisible()
                try {
                    searchedDataSubTitle = arrivalData.getOrNull(0)?.airlineName ?: "N/A"
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
                (activity as AirportSearchActivity).setAirportName()
            }
        }
    }

    private fun loadAdForTail() {
        NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber.apply {
            loadNativeAd(
                requireContext(),
                RemoteConfigManager.getBoolean("NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber")
            )
            showNativeAd(
                adGroup = NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber,
                frameLayout = binding.flAdplaceholder,
                adLayout = R.layout.native_ad_layout_view_with_media,
                activity = requireActivity() as AppCompatActivity
            )
        }
    }

    private fun showDialogPremium() {
        CustomDialogBuilder(requireContext())
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
            }.show()
    }

    private fun showPremiumScreen() {
        config.startDiscountIfNeeded()

        val isDiscountActive = config.isDiscountActive()

        if (isDiscountActive) {
            val intent = Intent(requireContext(), PremiumActivity2::class.java)
            intent.putExtra("from_arrival", true)

            startActivity(intent)
        } else {
            val intent = Intent(requireContext(), PremiumActivity::class.java)
            intent.putExtra("from_arrival", true)
            startActivity(intent)
        }
    }

    private fun showRewardedAd() {
        val rewardedArrival =
            RemoteConfigManager.getBoolean("REWARDED_ARRIVAL")
        if (rewardedArrival) {
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                rewardedAd.loadAndShowRewardedAd(
                    requireActivity(),
                    app.getString(R.string.REWARDED_ARRIVAL),
                    onRewardEarned = {
                        startActivity(Intent(requireContext(), DetailActivityForSearch::class.java))
                    }, {
                        startActivity(Intent(requireContext(), DetailActivityForSearch::class.java))
                    }
                )
            }
        } else {
            startActivity(Intent(requireContext(), DetailActivityForSearch::class.java))
        }
    }

    private fun addAdToArrivalData(): ArrayList<FullDetailFlightData> {
        val arrData = ArrayList<FullDetailFlightData>()
        arrivalData.forEachIndexed { index, data ->
            val nativeARRIVALFLIGHTForAirportOrAirline =
                RemoteConfigManager.getBoolean("NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline")
            if (nativeARRIVALFLIGHTForAirportOrAirline) {
                if (index == 1) {
                    arrData.add(data.copy(type = 2))
                }
                if (index % 2 == 1 && index > 2) {
                    arrData.add(data.copy(type = 2))
                }
            }

            arrData.add(data.copy(type = 1))
        }
        return arrData
    }

    override fun onResume() {
        super.onResume()
        searchedDataSubTitle = arrivalData.getOrNull(0)?.airlineName ?: "N/A"
        (activity as AirportSearchActivity).setAirportName()
    }
}