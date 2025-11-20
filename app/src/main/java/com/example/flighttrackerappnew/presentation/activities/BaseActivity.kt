package com.example.flighttrackerappnew.presentation.activities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.example.flighttrackerappnew.FlightApp
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.LocaleHelper
import com.example.flighttrackerappnew.presentation.utils.hideNavigationBar
import com.example.flighttrackerappnew.presentation.utils.setStatusBarDisplay
import org.koin.android.ext.android.inject

abstract class BaseActivity<BINDING : ViewBinding>(private val bindingInflater: (LayoutInflater) -> BINDING) :
    AppCompatActivity() {
    lateinit var binding: BINDING
    lateinit var app: FlightApp

    val config: Config by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as FlightApp
        binding = bindingInflater(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setStatusBarDisplay(isStatusBarBgLight = false, window = window)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            if (statusBarHeight > 0) {
                v.setPadding(0, statusBarHeight, 0, 0)
            }
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        window.hideNavigationBar()
    }

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, config.selectedLanguageCode)
        super.attachBaseContext(context)
    }
}