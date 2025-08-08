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
import com.example.flighttrackerappnew.presentation.activities.WelcomeActivity
import com.example.flighttrackerappnew.presentation.adManager.controller.NativeAdController
import com.example.flighttrackerappnew.presentation.remoteconfig.RemoteConfigManager
import com.example.flighttrackerappnew.presentation.utils.visible
import org.koin.android.ext.android.inject
import kotlin.getValue

class OnBoarding4Fragment : Fragment() {
    private val binding: FragmentOnBoarding4Binding by lazy {
        FragmentOnBoarding4Binding.inflate(layoutInflater)
    }

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

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this@OnBoarding4Fragment.context, WelcomeActivity::class.java))
        }

        if (showWelcome){
            val app = (requireActivity() as? BaseActivity<*>)?.app
            app?.let {
                nativeAdController.loadWelcomeScreenNativeAd(
                    requireContext(),
                    app.getString(R.string.NATIVE_WELCOME)
                )
            }
        }
        if (showOB4) {
            binding.flAdplaceholder.visible()
            nativeAdController.showOnb4NativeAd(
                requireContext(),
                binding.flAdplaceholder
            )
        }

    }
}