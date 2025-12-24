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
import com.example.flighttrackerappnew.presentation.activities.beforeHome.MapStyleActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.WelcomeActivity
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.NATIVE_MAP
import com.example.flighttrackerappnew.presentation.admob.native.NativeAdProvider.native_OnBoarding6
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible
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
        setLayout()
    }

    private fun setLayout() {
        binding.apply {
            if (RemoteConfigManager.getBoolean("native_OnBoarding6") && !config.isPremiumUser) {
                navTop.invisible()
                navBottom.visible()
                showAd()
            } else if (config.isPremiumUser) {
                navTop.visible()
                navBottom.invisible()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        native_OnBoarding6.loadNativeAd(
            requireContext(),
            RemoteConfigManager.getBoolean("native_OnBoarding6")
        )
    }

    private fun showAd() {
        native_OnBoarding6.showNativeAd(
            adGroup = native_OnBoarding6,
            frameLayout = binding.flAdplaceholder,
            adLayout = R.layout.native_ad_layout_view_with_media,
            activity = requireActivity() as AppCompatActivity
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