package com.example.flighttrackerappnew.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.databinding.FragmentOnBoarding4Binding
import com.example.flighttrackerappnew.presentation.activities.BaseActivity
import com.example.flighttrackerappnew.presentation.activities.beforeHome.WelcomeActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.invisible
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject
import kotlin.getValue

class OnBoarding4Fragment : Fragment() {
    private val binding: FragmentOnBoarding4Binding by lazy {
        FragmentOnBoarding4Binding.inflate(layoutInflater)
    }
    private val config: Config by inject()

    private val nativeAdController: NativeAdController by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val showOB4 =
            RemoteConfigManager.getBoolean("NATIVE_ONB4")
        val showWelcome =
            RemoteConfigManager.getBoolean("NATIVE_WELCOME")

        binding.apply {
            btnNext.setOnClickListener {
                startActivity(Intent(this@OnBoarding4Fragment.context, WelcomeActivity::class.java))
            }

            conNext.setOnClickListener {
                startActivity(Intent(this@OnBoarding4Fragment.context, WelcomeActivity::class.java))
            }
        }

        if (showWelcome && !config.isPremiumUser) {
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                nativeAdController.loadWelcomeScreenNativeAd(
                    requireContext(),
                    app.getString(R.string.NATIVE_WELCOME)
                )
            }
        }
        binding.apply {
            if (showOB4 && !config.isPremiumUser) {
                binding.flAdplaceholder.visible()
                navTop.invisible()
                navBottom.visible()
                nativeAdController.showOnb4NativeAd(
                    requireContext(),
                    binding.flAdplaceholder
                )
            } else {
                navTop.visible()
                navBottom.invisible()
            }
        }
    }
}