package com.example.flighttrackerappnew.presentation.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.example.flighttrackerappnew.FlightApp
import com.example.flighttrackerappnew.R
import com.example.flighttrackerappnew.presentation.dialogbuilder.CustomDialogBuilder
import com.example.flighttrackerappnew.presentation.helper.Config
import com.example.flighttrackerappnew.presentation.utils.LocaleHelper
import com.example.flighttrackerappnew.presentation.utils.hideSystemUI
import com.example.flighttrackerappnew.presentation.utils.setScreenDisplay
import com.example.flighttrackerappnew.presentation.utils.showToast
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import org.koin.android.ext.android.inject

abstract class BaseActivity<BINDING : ViewBinding>(private val bindingInflater: (LayoutInflater) -> BINDING) :
    AppCompatActivity() {
    lateinit var binding: BINDING
    lateinit var app: FlightApp

    val config: Config by inject()

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (fineLocationGranted || coarseLocationGranted) {
                this.showToast("Permission Granted")
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialog()
                } else {
                    showDialogSetting()
                }
            }
        }

    private fun showDialog() {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_location_permission)
            .setCancelable(false)
            .setPositiveClickListener {
                requestLocationPermission()
                it.dismiss()
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    private fun showDialogSetting() {
        CustomDialogBuilder(this)
            .setLayout(R.layout.dialog_location_permission_setting)
            .setCancelable(false)
            .setPositiveClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
                it.dismiss()
            }.setNegativeClickListener {
                it.dismiss()
            }.show()
    }

    fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    var interval: Long = 0
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = interval
    }

    fun getRemoteConfig(): FirebaseRemoteConfig {
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        return remoteConfig
    }

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

    fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}