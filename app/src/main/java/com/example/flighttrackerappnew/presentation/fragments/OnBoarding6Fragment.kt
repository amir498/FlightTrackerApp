package com.example.flighttrackerappnew.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding6Binding
import com.example.flighttrackerappnew.presentation.activities.MapStyleActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.WelcomeActivity
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_MAP
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_OnBoarding6
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import org.koin.android.ext.android.inject

class OnBoarding6Fragment : Fragment() {
    private val binding: FragmentOnBoarding6Binding by lazy {
        FragmentOnBoarding6Binding.inflate(layoutInflater)
    }
    private val config: Config by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewListener()
        showAd()
        loadAd()
    }

    private fun showAd(){
        native_OnBoarding6.showNativeAd(
            adGroup = native_OnBoarding6,
            frameLayout = binding.flAdplaceholder,
            adLayout = R.layout.native_ad_layout_view_with_media,
          activity =   requireActivity() as AppCompatActivity
        )
    }

    private fun loadAd() {
        NATIVE_MAP.loadNativeAd(
            requireContext(),
            RemoteConfigManager.getBoolean("NATIVE_MAP")
        )
    }

    private fun viewListener() {
        binding.apply {
            if (config.isPremiumUser) {
                btnNext.setOnClickListener {
                    startActivity(
                        Intent(
                            this@OnBoarding6Fragment.context,
                            MapStyleActivity::class.java
                        )
                    )
                }

                conNext.setOnClickListener {
                    startActivity(
                        Intent(
                            this@OnBoarding6Fragment.context,
                            MapStyleActivity::class.java
                        )
                    )
                }
            } else {
                btnNext.setOnClickListener {
                    startActivity(
                        Intent(
                            this@OnBoarding6Fragment.context,
                            WelcomeActivity::class.java
                        )
                    )
                }

                conNext.setOnClickListener {
                    startActivity(
                        Intent(
                            this@OnBoarding6Fragment.context,
                            WelcomeActivity::class.java
                        )
                    )
                }
            }

        }

    }
}