plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.flighttrackerappnew"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.radar.flight.tracker.airport.info"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

//    signingConfigs {
//        create("release") {
//            storeFile = file("D:/FindLostPhone/key/key.jks")
//            storePassword =
//                "FindLostPhone"
//            keyAlias = "key0"
//            keyPassword = "FindLostPhone"
//        }
//    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            resValue("string", "INTERSTITIAL_SPLASH", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "BANNER_SPLASH", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "NATIVE1_LANGUAGESCREEN1", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE2_LANGUAGESCREEN1", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE1_LANGUAGESCREEN2", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE2_LANGUAGESCREEN2", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_ONB1", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_ONB4", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_ONB_Full1", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_ONB_Full2", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_WELCOME", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_MAP", "ca-app-pub-3940256099942544/2247696110")

            resValue("string", "BANNER_HOME", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "NATIVE_HOME", "ca-app-pub-3940256099942544/2247696110")

            resValue("string", "NATIVE_SETTING", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "BANNER_LIVE_MAP", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "NATIVE_SEARCH_ACTIVITY", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_SAVED_FLIGHT", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_TRACKED_FLIGHT", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "BANNER_NEARBy_AIRPORT", "ca-app-pub-3940256099942544/9214589741")

            resValue("string", "BANNER_SEARCH_AIRPORT", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "BANNER_SEARCH_AIRCRAFT", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "BANNER_SEARCH_AIRLINE", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "BANNER_SEARCH_TAIL", "ca-app-pub-3940256099942544/9214589741")

            resValue(
                "string",
                "NATIVE_ARRIVAL_FLIGHT_For_Airport_Or_Airline",
                "ca-app-pub-3940256099942544/2247696110"
            )
            resValue(
                "string",
                "NATIVE_ARRIVAL_FLIGHT_For_Aircraft_Or_TailNumber",
                "ca-app-pub-3940256099942544/2247696110"
            )
            resValue(
                "string",
                "NATIVE_DEPARTURE_FLIGHT_For_Airport_Or_Airline",
                "ca-app-pub-3940256099942544/2247696110"
            )
            resValue(
                "string",
                "NATIVE_DEPARTURE_FLIGHT_For_Aircraft_Or_TailNumber",
                "ca-app-pub-3940256099942544/2247696110"
            )

            resValue("string", "BANNER_DETAIL", "ca-app-pub-3940256099942544/9214589741")

            resValue("string", "APP_OPEN", "ca-app-pub-3940256099942544/9257395921")
            resValue("string", "REWARDED_ARRIVAL", "ca-app-pub-3940256099942544/5224354917")
            resValue("string", "REWARDED_DEPARTURE", "ca-app-pub-3940256099942544/5224354917")
            resValue("string", "REWARDED_FAV", "ca-app-pub-3940256099942544/5224354917")
            resValue("string", "REWARDED_FOLLOW", "ca-app-pub-3940256099942544/5224354917")
            resValue("string", "REWARDED_LIVE", "ca-app-pub-3940256099942544/5224354917")
            resValue("string", "NATIVE_FLIGHT_SCHEDULED_SEARCH", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_FLIGHT_SCHEDULED_TYPE", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "NATIVE_FLIGHT_SCHEDULED", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "INTERSTITIAL_HOME", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "INTERSTITIAL_SEARCH", "ca-app-pub-3940256099942544/1033173712")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("release")
//            resValue("string", "INTERSTITIAL_SPLASH", "ca-app-pub-3940256099942544/1033173712")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //coroutine
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.room.common.jvm)

    //roomDb
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    //koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    //Glide
    api(libs.glide)

    //lottie
    implementation(libs.lottie)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //ssp,sdp
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    //google map
    implementation(libs.play.services.maps)

    //navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.kotlin.reflect)

    implementation(libs.play.services.location)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    //UMP SDK
    implementation(libs.user.messaging.platform)

    //Google Mobile Ads Sdk
    implementation(libs.play.services.ads)

    //shimmer
    implementation(libs.shimmer)

    implementation(libs.firebase.config)
    implementation(libs.google.firebase.analytics)

    implementation(libs.androidx.lifecycle.process)

}