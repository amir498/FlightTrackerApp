package com.example.flighttrackerappnew.presentation.activities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.flighttrackerappnew.FlightApp
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.LocaleHelper
import com.example.flighttrackerappnew.presentation.utils.hideSystemUI
import com.example.flighttrackerappnew.presentation.utils.setScreenDisplay
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

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

//        window.setStatusAndNavigationBarColor(this, R.color.app_bg_top, R.color.app_bg_bottom)
        window.hideSystemUI()

        setScreenDisplay()
    }

    override fun attachBaseContext(newBase: Context) {
        val context = LocaleHelper.setLocale(newBase, config.selectedLanguageCode)
        super.attachBaseContext(context)
    }
}