# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep your model classes
-keep class com.example.flighttrackerappnew.data.** { *; }
-keep class com.example.flighttrackerappnew.domain.** { *; }
-keep class com.example.flighttrackerappnew.presentation.** { *; }
-keep class com.example.flighttrackerappnew.FlightApp { *; }

# Preserve generic type info for Gson
-keepattributes Signature, InnerClasses, EnclosingMethod, Exceptions

# Keep all TypeToken subclasses
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# Keep Gson internals and annotations
-keep class com.google.gson.** { *; }
-keep class com.google.gson.internal.** { *; }
-keepattributes *Annotation*

# Retrofit + Gson converter
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepclassmembers class **$TypeAdapterFactory { *; }
-keep class **$TypeAdapterFactory { *; }

# Keep Gson TypeToken subclasses (stronger version)
-keep class * extends com.google.gson.reflect.TypeToken {
    <fields>;
    <methods>;
}

# Keep generic signatures on everything (R8 can be aggressive)
-keepattributes Signature, InnerClasses, EnclosingMethod

# Prevent obfuscation of anonymous inner classes inside data package
-keep class com.example.flighttrackerappnew.data.**$* { *; }

################################
## 🧩 GENERAL ANDROID KEEP RULES
################################
#
## Keep your Application class (Android needs this)
#-keep class com.example.flighttrackerappnew.FlightApp { *; }
#
## Keep Android entry points
#-keep class ** extends android.app.Application { *; }
#-keep class ** extends android.app.Activity { *; }
#-keep class ** extends android.app.Service { *; }
#-keep class ** extends android.content.BroadcastReceiver { *; }
#-keep class ** extends android.content.ContentProvider { *; }
#
## Keep annotations and signatures (used by Retrofit, Gson, Room, etc.)
#-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, AnnotationDefault
#
################################
## ⚙️ COROUTINES
################################
#-dontwarn kotlinx.coroutines.**
#-keep class kotlinx.coroutines.** { *; }
#
################################
## 🏛 ROOM (AndroidX Room Database)
################################
## Keep Room entities, DAOs, and database
#-keep class androidx.room.** { *; }
#-keep class * extends androidx.room.RoomDatabase { *; }
#-keep @androidx.room.Dao class * { *; }
#-keep class **_Impl { *; }  # Generated Room classes
#
## Keep entities and fields annotated with @Entity, @PrimaryKey, etc.
#-keepattributes *Annotation*
#
################################
## 🧠 KOIN (Dependency Injection)
################################
#-keep class org.koin.** { *; }
#-keep class * implements org.koin.core.module.Module { *; }
#-keep class * extends org.koin.core.module.Module { *; }
#-dontwarn org.koin.**
#
## Keep Kotlin reflection (Koin uses reflection for DI)
#-keep class kotlin.reflect.** { *; }
#
################################
## ☁️ RETROFIT + OKHTTP + GSON
################################
#-dontwarn okio.**
#-dontwarn okhttp3.**
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-keep class retrofit2.** { *; }
#-keep interface retrofit2.** { *; }
#
## Keep GSON models
#-keep class com.example.flighttrackerappnew.data.model.** { *; }
#-keep class com.google.gson.** { *; }
#-keep class com.google.gson.reflect.TypeToken { *; }
#-keep class * extends com.google.gson.reflect.TypeToken { *; }
#-keepclassmembers class ** {
#    @com.google.gson.annotations.SerializedName <fields>;
#}
#-keepattributes *Annotation*
#
################################
## 🖼 GLIDE
################################
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep class com.bumptech.glide.** { *; }
#-dontwarn com.bumptech.glide.**
#
################################
## 🎨 LOTTIE
################################
#-keep class com.airbnb.lottie.** { *; }
#-dontwarn com.airbnb.lottie.**
#
################################
## 🔄 NAVIGATION
################################
#-keep class androidx.navigation.** { *; }
#-dontwarn androidx.navigation.**
#
################################
## 🗺 GOOGLE MAPS & LOCATION
################################
#-dontwarn com.google.android.gms.**
#-keep class com.google.android.gms.** { *; }
#
################################
## 💳 BILLING
################################
#-keep class com.android.billingclient.** { *; }
#-dontwarn com.android.billingclient.**
#
################################
## ✨ SHIMMER
################################
#-keep class com.facebook.shimmer.** { *; }
#-dontwarn com.facebook.shimmer.**
#
################################
## 🔥 FIREBASE
################################
#-keep class com.google.firebase.** { *; }
#-dontwarn com.google.firebase.**
#
################################
## 📢 GOOGLE MOBILE ADS + UMP
################################
#-keep class com.google.android.gms.ads.** { *; }
#-keep class com.google.android.ump.** { *; }
#-dontwarn com.google.android.gms.**
#-dontwarn com.google.android.ump.**
#
################################
## 📏 SDP & SSP
################################
#-keep class com.intuit.sdp.** { *; }
#-keep class com.intuit.ssp.** { *; }
#
################################
## 🧰 OPTIONAL SAFETY RULES
################################
## Keep enum names (Gson/Room may serialize them)
#-keepclassmembers enum * { *; }
#
## Suppress warnings from generated classes
#-dontwarn org.intellij.lang.annotations.**
#-dontwarn javax.annotation.**
#
################################
## 🧩 END OF RULES
################################
#
