package com.example.flighttrackerappnew.presentation.ump

import android.app.Activity
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class UMPConsentManager(private val activity: Activity) {

    private var consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(activity)

    fun checkConsent(onConsentResult: (Boolean) -> Unit) {
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("AE46A43A1CB75C82FD93B9FA308DE7C3")
            .build()

        val params: ConsentRequestParameters = ConsentRequestParameters.Builder()
//            .setConsentDebugSettings(debugSettings)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                Log.d("UMPConsentManager", "Consent info update Success")
                if (consentInformation.isConsentFormAvailable) {
                    Log.d(
                        "UMPConsentManager",
                        "consentInformation.isConsentFormAvailable:${consentInformation.isConsentFormAvailable}"
                    )
                    loadAndShowConsentForm(onConsentResult)
                } else {
                    Log.d(
                        "UMPConsentManager",
                        "consentInformation.isConsentFormNotAvailable:${consentInformation.isConsentFormAvailable}"
                    )
                    onConsentResult(true)
                }
            },
            { formError ->
                Log.d(
                    "UMPConsentManager",
                    "consentInformation.Consent Form Error:${formError.message}"
                )
                Log.d(
                    "UMPConsentManager",
                    "consentInformation.Consent Form Error:${formError.errorCode}"
                )
                onConsentResult(true)
            }
        )
    }

    private fun loadAndShowConsentForm(onConsentResult: (Boolean) -> Unit) {
        UserMessagingPlatform.loadConsentForm(
            activity,
            { consentForm ->
                consentForm.show(activity) {
                    if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                        onConsentResult(false)
                        Log.d(
                            "UMPConsentManager",
                            "Consent form REQUIRED: True"
                        )
                    } else {
                        onConsentResult(true)
                        Log.d(
                            "UMPConsentManager",
                            "Consent form REQUIRED False"
                        )
                    }
                }
            },
            { formError ->
                Log.d(
                    "UMPConsentManager",
                    "Consent form load failed:${formError.message}"
                )
                onConsentResult(true)
            }
        )
    }
}
